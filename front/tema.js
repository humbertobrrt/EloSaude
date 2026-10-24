/* tema.js — Modo escuro/claro do EloSaúde (JS puro, sem frameworks)
 * Uso: <script src="tema.js"></script> dentro do <head> (sem defer),
 * para o tema ser aplicado antes da página aparecer (sem "piscada").
 */
(function () {
  "use strict";

  var CHAVE = "elosaude-tema";
  var raiz = document.documentElement;

  /* ---------- 1. Leitura / gravação da preferência ---------- */
  function lerSalvo() {
    try { return localStorage.getItem(CHAVE); } catch (e) { return null; }
  }
  function salvar(tema) {
    try { localStorage.setItem(CHAVE, tema); } catch (e) { /* modo privado */ }
  }
  function temaInicial() {
    var salvo = lerSalvo();
    if (salvo === "dark" || salvo === "light") return salvo;
    var sistemaEscuro = window.matchMedia &&
      window.matchMedia("(prefers-color-scheme: dark)").matches;
    return sistemaEscuro ? "dark" : "light";
  }

  /* ---------- 2. CSS do modo escuro (injetado via JS) ---------- */
  var CSS = [
    /* Variáveis das telas internas (style.css) e de login (login.css) */
    'html[data-tema="dark"]{',
    '  color-scheme:dark;',
    '  --bg:#081a1c; --card:#102426; --ink:#e6f2f0; --muted:#8fa9a6;',
    '  --pri:#17b09b; --pri2:#27c9b3; --acc:#ff7a6b; --line:#1c3a3b;',
    '  --login-bg:#081a1c; --login-card:#102426; --login-ink:#e6f2f0;',
    '  --login-muted:#8fa9a6; --login-primary:#17b09b;',
    '  --login-primary-hover:#27c9b3; --login-border:#1c3a3b;',
    '}',

    /* Fundo geral */
    'html[data-tema="dark"] body{background:var(--bg);color:var(--ink);}',
    'html[data-tema="dark"] body.login-body,',
    'html[data-tema="dark"] body.login-page,',
    'html[data-tema="dark"] body:has(.login-card){',
    '  background:radial-gradient(circle at top left,#0f3a32,transparent 45%) fixed,#081a1c;}',
    /* wrapper .login-page não pinta fundo próprio (evita o "retângulo") */
    'html[data-tema="dark"] .login-page{background:transparent;}',

    /* Login (claro): gradiente na página inteira, sem a "caixa" verde */
    'body:has(.login-page),body:has(.login-card){',
    '  background:radial-gradient(circle at top left,#d8f5e8,transparent 45%) fixed,var(--bg);}',
    'html body .login-page{background:transparent;}',

    /* Cabeçalho e navegação */
    'html[data-tema="dark"] .top{background:rgba(8,26,28,.85);}',
    'html[data-tema="dark"] nav a:hover{background:#16373a;color:var(--ink);}',
    'html[data-tema="dark"] nav a.on{background:var(--pri);color:#fff;}',

    /* Cartões e sombras */
    'html[data-tema="dark"] .card,',
    'html[data-tema="dark"] .login-card{box-shadow:0 20px 55px rgba(0,0,0,.45);}',

    /* Campos de formulário */
    'html[data-tema="dark"] input,',
    'html[data-tema="dark"] select,',
    'html[data-tema="dark"] textarea,',
    'html[data-tema="dark"] .login-form input,',
    'html[data-tema="dark"] .login-card input{',
    '  background:#0b1f21;color:var(--ink);border-color:var(--line);}',
    'html[data-tema="dark"] input::placeholder,',
    'html[data-tema="dark"] textarea::placeholder{color:#6f8a87;}',
    'html[data-tema="dark"] input:focus,',
    'html[data-tema="dark"] textarea:focus,',
    'html[data-tema="dark"] .login-card input:focus{',
    '  background:#0d2427;box-shadow:0 0 0 4px rgba(39,201,179,.18);}',

    /* Chrome autofill não deve voltar ao fundo claro */
    'html[data-tema="dark"] input:-webkit-autofill{',
    '  -webkit-box-shadow:0 0 0 100px #0b1f21 inset;',
    '  -webkit-text-fill-color:var(--ink);}',

    /* Botões secundários, tags e selos de status */
    'html[data-tema="dark"] button.ghost,',
    'html[data-tema="dark"] .tag{background:#16373a;color:#7fe0d0;}',
    'html[data-tema="dark"] .s-Solicitada{background:#3a2a14;color:#ffb866;}',
    'html[data-tema="dark"] .s-Em{background:#16264a;color:#8fb0ff;}',
    'html[data-tema="dark"] .s-Agendada{background:#2a1c4a;color:#c1a3ff;}',
    'html[data-tema="dark"] .s-Concluída{background:#0f3324;color:#5fdc9f;}',

    /* Erro, aviso (toast) e rodapé */
    'html[data-tema="dark"] .login-error{color:#ff8a7a;}',
    'html[data-tema="dark"] .toast{background:#e6f2f0;color:#081a1c;}',
    'html[data-tema="dark"] a{color:var(--pri2);}',
    'html[data-tema="dark"] nav a{color:var(--muted);}',
    'html[data-tema="dark"] nav a.on{color:#fff;}',
    'html[data-tema="dark"] .logo{color:var(--ink);}',

    /* Transição suave só depois do 1º carregamento */
    'html.tema-anima,html.tema-anima *{transition:background-color .3s,',
    '  color .3s,border-color .3s !important;}',

    /* Botão de alternância: discreto, só contorno */
    'html body button.tema-toggle{',
    '  display:inline-flex;align-items:center;gap:8px;',
    '  padding:8px 14px;border:1px solid var(--line,#dce8e6);',
    '  border-radius:999px;cursor:pointer;font:inherit;font-size:.85rem;',
    '  font-weight:600;background:transparent;color:var(--muted,#5b7276);',
    '  box-shadow:none;transform:none;',
    '  transition:color .2s,border-color .2s;}',
    'html body button.tema-toggle:hover{background:transparent;',
    '  color:var(--pri,#0e8a7a);border-color:var(--pri,#0e8a7a);',
    '  transform:none;}',
    'html body button.tema-toggle:focus-visible{outline:2px solid var(--pri2,#13b39c);',
    '  outline-offset:2px;}',
    /* Posição flutuante quando a página não tem cabeçalho (login/cadastro) */
    'html body button.tema-toggle.flutuante{position:fixed;top:16px;',
    '  right:16px;z-index:1000;}'
  ].join("\n");

  function injetarCSS() {
    var estilo = document.createElement("style");
    estilo.id = "tema-estilos";
    estilo.textContent = CSS;
    document.head.appendChild(estilo);
  }

  /* ---------- Marca no card de login ---------- */
  // Se o card mostra o quadrado verde com "+", troca pelo ícone de cruz (PNG).
  function corrigirMarca() {
    var card = document.querySelector(".login-brand, .login-card");
    if (!card || card.querySelector("img")) return;

    var alvo = card.querySelector(
      ".login-symbol, [class*='symbol'], [class*='logo'], [class*='mark'], [class*='icon']"
    );
    if (!alvo) {
      var filhos = card.querySelectorAll("div, span");
      for (var i = 0; i < filhos.length; i++) {
        if (filhos[i].textContent.trim() === "+") { alvo = filhos[i]; break; }
      }
    }
    if (!alvo) return;

    var img = document.createElement("img");
    img.className = "login-logo";
    img.src = "img/elosaude-icone%20(1).png";
    img.alt = "";
    img.width = 64;
    img.height = 64;
    img.style.cssText =
      "display:block;margin:0 auto 14px;width:64px;height:64px;object-fit:contain";
    alvo.parentNode.replaceChild(img, alvo);
  }

  /* ---------- 3. Aplicar tema ---------- */
  var botao = null;

  function atualizarBotao(tema) {
    if (!botao) return;
    var escuro = tema === "dark";
    botao.textContent = escuro ? "☀️" : "🌙";
    botao.setAttribute("aria-pressed", String(escuro));
    botao.setAttribute("aria-label",
      escuro ? "Ativar modo claro" : "Ativar modo escuro");
  }

  function aplicar(tema) {
    raiz.setAttribute("data-tema", tema);
    var meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.setAttribute("content", tema === "dark" ? "#081a1c" : "#f4f8f7");
    atualizarBotao(tema);
  }

  function alternar() {
    raiz.classList.add("tema-anima");
    var novo = raiz.getAttribute("data-tema") === "dark" ? "light" : "dark";
    salvar(novo);
    aplicar(novo);
  }

  /* ---------- 4. Botão na página ---------- */
  function criarBotao() {
    botao = document.createElement("button");
    botao.type = "button";
    botao.className = "tema-toggle";
    botao.addEventListener("click", alternar);

    var cabecalho = document.querySelector("header.top");
    if (cabecalho) {
      cabecalho.appendChild(botao);
    } else {
      botao.classList.add("flutuante");
      document.body.appendChild(botao);
    }
    atualizarBotao(raiz.getAttribute("data-tema"));
  }

  /* ---------- 5. Inicialização ---------- */
  // Aplica já (antes do <body> existir) para evitar a piscada branca.
  injetarCSS();
  aplicar(temaInicial());

  function aoCarregar() {
    corrigirMarca();
    criarBotao();
    if (window.MutationObserver) {
      new MutationObserver(corrigirMarca)
        .observe(document.body, { childList: true, subtree: true });
    }
  }
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", aoCarregar);
  } else {
    aoCarregar();
  }

  // Sincroniza entre abas/páginas abertas
  window.addEventListener("storage", function (e) {
    if (e.key === CHAVE && (e.newValue === "dark" || e.newValue === "light")) {
      aplicar(e.newValue);
    }
  });

  // Se o usuário nunca escolheu, acompanha o tema do sistema
  if (window.matchMedia) {
    var mq = window.matchMedia("(prefers-color-scheme: dark)");
    var seguir = function (e) {
      if (!lerSalvo()) aplicar(e.matches ? "dark" : "light");
    };
    if (mq.addEventListener) mq.addEventListener("change", seguir);
    else if (mq.addListener) mq.addListener(seguir);
  }
})();
