const TOKEN_KEY = 'gameshelf-token';
const USER_KEY = 'gameshelf-user';
let token = localStorage.getItem(TOKEN_KEY) || localStorage.getItem('playpulse-token') || '';
let username = localStorage.getItem(USER_KEY) || localStorage.getItem('playpulse-user') || '';
let authMode = 'login';
let catalogue = [];
const page = document.documentElement.dataset.page;
const $ = id => document.getElementById(id);
const escapeHTML = value => String(value ?? '').replace(/[&<>'"]/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[char]));
const formatDate = value => {
  if (!value) return 'Recently';
  const date = new Date(value);
  return Number.isNaN(date.valueOf()) ? 'Recently' : date.toLocaleDateString(undefined, {month: 'short', day: 'numeric'});
};

const api = async (path, options = {}) => {
  const headers = {...(options.headers || {}), ...(token ? {Authorization: `Bearer ${token}`} : {})};
  if (options.body) headers['Content-Type'] = 'application/json';
  const response = await fetch(`/api/${path}`, {...options, headers});
  let payload = {};
  try { payload = await response.json(); } catch {}
  if (!response.ok || payload.code !== 200) throw new Error(payload.message || 'The service could not complete that request.');
  return payload.data;
};

function setMessage(id, text = '', error = false) {
  const element = $(id);
  if (!element) return;
  element.textContent = text;
  element.classList.toggle('error', error);
}

function announce(text, error = false) {
  let status = $('app-status');
  if (!status) {
    status = document.createElement('div');
    status.id = 'app-status';
    status.className = 'app-status';
    status.setAttribute('role', error ? 'alert' : 'status');
    status.setAttribute('aria-live', error ? 'assertive' : 'polite');
    document.body.append(status);
  }
  status.classList.toggle('error', error);
  status.setAttribute('role', error ? 'alert' : 'status');
  status.textContent = text;
  status.hidden = false;
  window.clearTimeout(announce.timer);
  announce.timer = window.setTimeout(() => { status.hidden = true; }, 4200);
}

async function withBusy(form, busyText, action) {
  const button = form.querySelector('[type="submit"]');
  const original = button.textContent;
  button.disabled = true;
  button.textContent = busyText;
  form.setAttribute('aria-busy', 'true');
  try { return await action(); }
  finally {
    button.disabled = false;
    button.textContent = original;
    form.removeAttribute('aria-busy');
  }
}

function clearInvalid(form) {
  form.querySelectorAll('[aria-invalid="true"]').forEach(field => field.removeAttribute('aria-invalid'));
}

function showInvalid(form, fields, messageId, message) {
  clearInvalid(form);
  fields.forEach(field => field.setAttribute('aria-invalid', 'true'));
  setMessage(messageId, message, true);
  fields[0]?.focus();
}

function gameById(id) { return catalogue.find(game => Number(game.id) === Number(id)); }

function gameArt(game, compact = false) {
  const initials = escapeHTML(game.title.split(' ').map(word => word[0]).join('').slice(0, 3));
  return `<div class="game-art tone-${escapeHTML(game.coverTone)} ${compact ? 'compact' : ''}" aria-hidden="true"><span>${initials}</span></div>`;
}

function syncSession() {
  const name = $('session-name');
  const link = $('account-link');
  const button = $('signout');
  if (name) name.textContent = token ? `Signed in as ${username || 'player'}` : 'Guest session';
  if (link) link.hidden = Boolean(token);
  if (button) button.hidden = !token;
  if (page === 'submit') {
    setMessage('game-submit-message', token ? 'Add only games that are missing from the catalogue.' : 'Sign in before adding a game.');
    setMessage('submit-message', token ? 'Write a specific note that helps another player decide.' : 'Sign in before publishing a review.');
  }
  if (page === 'account') {
    $('account-forms').hidden = Boolean(token);
    $('account-signed-in').hidden = !token;
    if (token) {
      $('account-user').textContent = `${username || 'Player'}'s library`;
      loadMyLibrary();
      loadMyReviews();
    }
  }
}

function signOut() {
  [TOKEN_KEY, USER_KEY, 'playpulse-token', 'playpulse-user'].forEach(key => localStorage.removeItem(key));
  token = '';
  username = '';
  syncSession();
}

async function loadCatalogue(search = '') {
  catalogue = await api(`games${search ? `?search=${encodeURIComponent(search)}` : ''}`);
  return catalogue;
}

function gameCard(game) {
  return `<article class="game-card">${gameArt(game)}<div class="game-card-copy"><p class="eyebrow">${escapeHTML(game.genres)} / ${game.releaseYear}</p><h3>${escapeHTML(game.title)}</h3><p>${escapeHTML(game.summary)}</p><div class="game-card-bottom"><a class="arrow-link" href="game.html?id=${game.id}">View game <b>&rarr;</b></a><button class="library-button" type="button" onclick="saveToLibrary(${game.id},'WISHLIST',this)">+ Library</button></div></div></article>`;
}

async function loadHome() {
  try {
    const [games, summary] = await Promise.all([loadCatalogue(), api('feedback/dashboard')]);
    $('metric-games').textContent = games.length;
    $('metric-reviews').textContent = summary.total || 0;
    $('metric-library').textContent = token ? 'Open' : 'Sign in';
    $('featured-games').innerHTML = games.slice(0, 3).map(gameCard).join('');
  } catch (error) {
    $('featured-games').innerHTML = `<div class="empty">Could not load the catalogue.<br>${escapeHTML(error.message)}</div>`;
    announce(error.message, true);
  }
}

async function loadGames() {
  const grid = $('game-grid');
  grid.setAttribute('aria-busy', 'true');
  grid.innerHTML = '<div class="skeleton"><i></i><i></i><i></i><span class="sr-only">Loading games</span></div>';
  try {
    const games = await loadCatalogue($('search').value.trim());
    $('result-count').textContent = `${games.length} game${games.length === 1 ? '' : 's'}`;
    grid.innerHTML = games.length ? games.map(gameCard).join('') : '<div class="empty">No games match this view.<br>Try another title or studio.</div>';
  } catch (error) {
    grid.innerHTML = `<div class="empty">Connection failed.<br>${escapeHTML(error.message)}</div>`;
  } finally { grid.removeAttribute('aria-busy'); }
}

async function saveToLibrary(id, status, button) {
  if (!token) { window.location.href = `account.html?next=${encodeURIComponent(location.href)}`; return; }
  const original = button?.textContent;
  if (button) { button.disabled = true; button.textContent = 'Saving...'; }
  try {
    await api(`games/${id}/library`, {method: 'POST', body: JSON.stringify({status})});
    if (button) button.textContent = 'In library';
    setMessage('game-message', 'Saved to your library.');
    announce('Saved to your library.');
  } catch (error) {
    if (button) button.textContent = original;
    setMessage('game-message', error.message, true);
    announce(error.message, true);
  } finally { if (button) button.disabled = false; }
}

function reviewCard(item) {
  const game = gameById(item.gameId) || {title: 'Game'};
  const id = Number(item.id);
  const votes = Number(item.voteCount || 0);
  const comments = Number(item.commentCount || 0);
  return `<article class="review"><div class="review-game">${gameArt(game, true)}<div><a href="game.html?id=${game.id || ''}">${escapeHTML(game.title)}</a><span>${escapeHTML(item.category || 'REVIEW')}</span></div></div><div class="review-copy"><h3>${escapeHTML(item.title)}</h3><p>${escapeHTML(item.description)}</p><div class="review-meta"><span>${formatDate(item.createdAt)}</span><button class="vote" type="button" data-votes="${votes}" onclick="vote(${id},this)" aria-label="Mark review helpful">Helpful ${votes}</button><button class="discussion-toggle" type="button" onclick="toggleDiscussion(${id},this)" aria-expanded="false" aria-controls="discussion-${id}">Discuss ${comments}</button></div><div class="discussion" id="discussion-${id}"></div></div></article>`;
}

async function loadReviews(gameId = null) {
  const list = $('review-list');
  if (!list) return;
  list.setAttribute('aria-busy', 'true');
  list.innerHTML = '<div class="skeleton"><i></i><i></i><span class="sr-only">Loading reviews</span></div>';
  try {
    if (!catalogue.length) await loadCatalogue();
    const result = await api('feedback?size=50&sort=popular');
    const reviews = (result.content || []).filter(item => !gameId || Number(item.gameId) === Number(gameId));
    list.innerHTML = reviews.length ? reviews.map(reviewCard).join('') : '<div class="empty">No reviews yet.<br>Be the first player to leave a useful note.</div>';
  } catch (error) {
    list.innerHTML = `<div class="empty">Could not load reviews.<br>${escapeHTML(error.message)}</div>`;
  } finally { list.removeAttribute('aria-busy'); }
}

async function loadGame() {
  const id = new URLSearchParams(location.search).get('id');
  if (!id) { location.href = 'board.html'; return; }
  try {
    const [game, games] = await Promise.all([api(`games/${id}`), loadCatalogue()]);
    catalogue = games;
    document.title = `${game.title} / GameShelf`;
    $('game-hero').innerHTML = `${gameArt(game)}<div><p class="eyebrow">${escapeHTML(game.studio)} / ${game.releaseYear}</p><h1>${escapeHTML(game.title)}</h1><p>${escapeHTML(game.summary)}</p><div class="game-actions"><button class="button" type="button" onclick="saveToLibrary(${game.id},'PLAYING',this)">Playing now</button><button class="button secondary" type="button" onclick="saveToLibrary(${game.id},'WISHLIST',this)">Add to wishlist</button><a class="arrow-link" href="submit.html?game=${game.id}">Write a review <b>&rarr;</b></a></div><p id="game-message" class="form-message" aria-live="polite"></p></div>`;
    await loadReviews(id);
  } catch (error) {
    $('game-hero').innerHTML = `<p class="form-message error">${escapeHTML(error.message)}</p>`;
  }
}

function fillGameSelect() {
  const select = $('game-id');
  if (!select) return;
  const requested = new URLSearchParams(location.search).get('game');
  select.innerHTML = catalogue.map(game => `<option value="${game.id}" ${String(game.id) === requested ? 'selected' : ''}>${escapeHTML(game.title)}</option>`).join('');
}

function setContributionTab(mode, focus = false) {
  const addMode = mode === 'game';
  const active = addMode ? $('add-game-tab') : $('review-tab');
  const inactive = addMode ? $('review-tab') : $('add-game-tab');
  active.classList.add('active');
  inactive.classList.remove('active');
  active.setAttribute('aria-selected', 'true');
  inactive.setAttribute('aria-selected', 'false');
  active.tabIndex = 0;
  inactive.tabIndex = -1;
  $('game-create-panel').hidden = !addMode;
  $('review-panel').hidden = addMode;
  if (focus) active.focus();
}

function setupTabKeyboard(container, activate) {
  container.addEventListener('keydown', event => {
    if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) return;
    event.preventDefault();
    const tabs = [...container.querySelectorAll('[role="tab"]')];
    const current = tabs.indexOf(document.activeElement);
    const next = event.key === 'Home' ? 0 : event.key === 'End' ? tabs.length - 1 : (current + (event.key === 'ArrowRight' ? 1 : -1) + tabs.length) % tabs.length;
    activate(tabs[next]);
  });
}

async function setupSubmit() {
  try {
    await loadCatalogue();
    fillGameSelect();
    const params = new URLSearchParams(location.search);
    setContributionTab(params.get('tab') === 'review' || params.has('game') ? 'review' : 'game');
  } catch (error) {
    setMessage('game-submit-message', `Catalogue unavailable: ${error.message}`, true);
    setMessage('submit-message', `Catalogue unavailable: ${error.message}`, true);
    announce(error.message, true);
  }
}

async function submitGame(event) {
  event.preventDefault();
  if (!token) { window.location.href = `account.html?next=${encodeURIComponent(location.href)}`; return; }
  const form = event.currentTarget;
  const title = $('game-title').value.trim();
  const studio = $('game-studio').value.trim();
  const releaseYear = Number($('game-year').value);
  const genres = $('game-genres').value.trim();
  const summary = $('game-summary').value.trim();
  const coverTone = $('game-tone').value;
  const invalid = [!title && $('game-title'), !studio && $('game-studio'), (!Number.isInteger(releaseYear) || releaseYear < 1950 || releaseYear > 2100) && $('game-year'), !genres && $('game-genres'), !summary && $('game-summary')].filter(Boolean);
  if (invalid.length) { showInvalid(form, invalid, 'game-submit-message', 'Complete every field and use a release year from 1950 to 2100.'); return; }
  clearInvalid(form);
  await withBusy(form, 'Adding game...', async () => {
    try {
      const game = await api('games', {method: 'POST', body: JSON.stringify({title, studio, releaseYear, genres, summary, coverTone})});
      setMessage('game-submit-message', 'Game added. Opening its catalogue page.');
      window.location.href = `game.html?id=${game.id}`;
    } catch (error) { setMessage('game-submit-message', error.message, true); }
  });
}

async function submitReview(event) {
  event.preventDefault();
  if (!token) { window.location.href = `account.html?next=${encodeURIComponent(location.href)}`; return; }
  const form = event.currentTarget;
  const title = $('title').value.trim();
  const description = $('description').value.trim();
  const invalid = [!title && $('title'), !description && $('description')].filter(Boolean);
  if (invalid.length) { showInvalid(form, invalid, 'submit-message', 'Add a headline and useful detail before publishing.'); return; }
  clearInvalid(form);
  await withBusy(form, 'Publishing...', async () => {
    try {
      await api('feedback', {method: 'POST', body: JSON.stringify({title, description, gameId: Number($('game-id').value), category: $('category').value, priority: 'MEDIUM'})});
      form.reset();
      fillGameSelect();
      setMessage('submit-message', 'Review published. It now appears on the game page.');
    } catch (error) { setMessage('submit-message', error.message, true); }
  });
}

async function loadMyLibrary() {
  const list = $('my-library');
  list.setAttribute('aria-busy', 'true');
  list.innerHTML = '<div class="skeleton"><i></i><i></i><span class="sr-only">Loading library</span></div>';
  try {
    const rows = await api('games/library/mine');
    list.innerHTML = rows.length ? rows.map(item => `<article class="library-row">${gameArt(item.game, true)}<div><p class="eyebrow">${escapeHTML(String(item.status).replace('_', ' '))}</p><h3>${escapeHTML(item.game.title)}</h3><p>${escapeHTML(item.game.studio)} / ${item.game.releaseYear}</p></div><a class="arrow-link" href="game.html?id=${item.game.id}">Open <b>&rarr;</b></a></article>`).join('') : '<div class="empty">Your library is empty.<br>Browse the catalogue and save a game.</div>';
  } catch (error) { list.innerHTML = `<p class="form-message error">${escapeHTML(error.message)}</p>`; }
  finally { list.removeAttribute('aria-busy'); }
}

async function loadMyReviews() {
  const list = $('my-reviews');
  if (!list) return;
  list.setAttribute('aria-busy', 'true');
  try {
    if (!catalogue.length) await loadCatalogue();
    const result = await api('feedback/mine?size=10');
    list.innerHTML = (result.content || []).length ? result.content.map(reviewCard).join('') : '<div class="empty">No reviews from you yet.</div>';
  } catch (error) { list.innerHTML = `<p class="form-message error">${escapeHTML(error.message)}</p>`; }
  finally { list.removeAttribute('aria-busy'); }
}

async function vote(id, button) {
  if (!token) { window.location.href = `account.html?next=${encodeURIComponent(location.href)}`; return; }
  button.disabled = true;
  try {
    const result = await api(`feedback/${id}/votes`, {method: 'POST'});
    const previous = Number(button.dataset.votes || 0);
    const next = Math.max(0, previous + (result.voted ? 1 : -1));
    button.dataset.votes = next;
    button.textContent = `Helpful ${next}`;
    button.setAttribute('aria-pressed', String(Boolean(result.voted)));
    announce(result.voted ? 'Marked as helpful.' : 'Helpful vote removed.');
  } catch (error) { announce(error.message, true); }
  finally { button.disabled = false; }
}

async function toggleDiscussion(id, button) {
  const box = $(`discussion-${id}`);
  if (box.classList.contains('open')) {
    box.classList.remove('open');
    button.setAttribute('aria-expanded', 'false');
    return;
  }
  box.classList.add('open');
  button.setAttribute('aria-expanded', 'true');
  box.innerHTML = '<div class="comment">Loading discussion...</div>';
  try {
    const comments = await api(`feedback/${id}/comments`);
    box.innerHTML = (comments.length ? comments.map(comment => `<div class="comment">${escapeHTML(comment.content)}<small>Player ${escapeHTML(comment.userId)} / ${formatDate(comment.createdAt)}</small></div>`).join('') : '<div class="comment">No replies yet. Start a useful discussion.</div>') + (token ? `<form class="comment-form" onsubmit="postComment(event,${id})"><input required maxlength="1000" aria-label="Add a comment" placeholder="Add to the discussion"><button class="button" type="submit">Reply</button></form>` : '<div class="comment"><small>Sign in to add a reply.</small></div>');
  } catch (error) { box.innerHTML = `<div class="comment">Could not load discussion. ${escapeHTML(error.message)}</div>`; }
}

async function postComment(event, id) {
  event.preventDefault();
  const form = event.currentTarget;
  const input = form.querySelector('input');
  const content = input.value.trim();
  if (!content) return;
  await withBusy(form, 'Replying...', async () => {
    try {
      await api(`feedback/${id}/comments`, {method: 'POST', body: JSON.stringify({content})});
      const toggle = document.querySelector(`[aria-controls="discussion-${id}"]`);
      const box = $(`discussion-${id}`);
      box.classList.remove('open');
      await toggleDiscussion(id, toggle);
    } catch (error) {
      input.setCustomValidity(error.message);
      input.reportValidity();
      input.addEventListener('input', () => input.setCustomValidity(''), {once: true});
    }
  });
}

function setAuthMode(mode, preserveMessage = false) {
  authMode = mode;
  const login = $('login-tab');
  const register = $('register-tab');
  const loginMode = mode === 'login';
  login.classList.toggle('active', loginMode);
  register.classList.toggle('active', !loginMode);
  login.setAttribute('aria-selected', String(loginMode));
  register.setAttribute('aria-selected', String(!loginMode));
  login.tabIndex = loginMode ? 0 : -1;
  register.tabIndex = loginMode ? -1 : 0;
  $('auth-submit').textContent = loginMode ? 'Sign in' : 'Create account';
  $('auth-password').autocomplete = loginMode ? 'current-password' : 'new-password';
  if (!preserveMessage) setMessage('auth-message');
}

async function handleAuth(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const user = $('auth-username').value.trim();
  const password = $('auth-password').value;
  const invalid = [user.length < 3 && $('auth-username'), password.length < 4 && $('auth-password')].filter(Boolean);
  if (invalid.length) { showInvalid(form, invalid, 'auth-message', 'Use at least 3 characters for your username and 4 for your password.'); return; }
  clearInvalid(form);
  await withBusy(form, authMode === 'login' ? 'Signing in...' : 'Creating account...', async () => {
    try {
      const data = await api(`auth/${authMode}`, {method: 'POST', body: JSON.stringify({username: user, password})});
      if (authMode === 'register') {
        setAuthMode('login', true);
        setMessage('auth-message', 'Account created. You can sign in now.');
        $('auth-password').value = '';
        $('auth-password').focus();
        return;
      }
      token = data.token;
      username = data.username || user;
      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, username);
      const next = new URLSearchParams(location.search).get('next');
      if (next) { window.location.href = next; return; }
      syncSession();
    } catch (error) { setMessage('auth-message', error.message, true); }
  });
}

function initMotion() {
  if (!('IntersectionObserver' in window) || window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;
  const sections = document.querySelectorAll('main > section');
  sections.forEach(section => section.classList.add('reveal-ready'));
  const observer = new IntersectionObserver(entries => entries.forEach(entry => {
    if (entry.isIntersecting) { entry.target.classList.add('reveal-in'); observer.unobserve(entry.target); }
  }), {threshold: .12});
  sections.forEach(section => observer.observe(section));
}

window.saveToLibrary = saveToLibrary;
window.vote = vote;
window.toggleDiscussion = toggleDiscussion;
window.postComment = postComment;

if ($('signout')) $('signout').addEventListener('click', signOut);
if ($('account-signout')) $('account-signout').addEventListener('click', signOut);

if (page === 'overview') loadHome();
if (page === 'board') {
  $('apply-filter').addEventListener('click', loadGames);
  $('search').addEventListener('keydown', event => { if (event.key === 'Enter') loadGames(); });
  loadGames();
}
if (page === 'game') loadGame();
if (page === 'submit') {
  setupSubmit();
  $('add-game-tab').addEventListener('click', () => setContributionTab('game'));
  $('review-tab').addEventListener('click', () => setContributionTab('review'));
  setupTabKeyboard(document.querySelector('.contribution-tabs'), tab => setContributionTab(tab.id === 'add-game-tab' ? 'game' : 'review', true));
  $('game-form').addEventListener('submit', submitGame);
  $('feedback-form').addEventListener('submit', submitReview);
}
if (page === 'account') {
  $('login-tab').addEventListener('click', () => setAuthMode('login'));
  $('register-tab').addEventListener('click', () => setAuthMode('register'));
  setupTabKeyboard(document.querySelector('#account-forms .tab-row'), tab => { setAuthMode(tab.id === 'login-tab' ? 'login' : 'register'); tab.focus(); });
  $('auth-form').addEventListener('submit', handleAuth);
}
syncSession();
initMotion();
