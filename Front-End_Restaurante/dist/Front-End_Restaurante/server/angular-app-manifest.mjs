
export default {
  bootstrap: () => import('./main.server.mjs').then(m => m.default),
  inlineCriticalCss: true,
  baseHref: '/',
  locale: undefined,
  routes: [
  {
    "renderMode": 2,
    "route": "/"
  },
  {
    "renderMode": 2,
    "route": "/seletor-login"
  },
  {
    "renderMode": 2,
    "route": "/login-funcionario"
  },
  {
    "renderMode": 2,
    "route": "/login-cliente"
  },
  {
    "renderMode": 2,
    "route": "/cardapio"
  },
  {
    "renderMode": 2,
    "route": "/pedidos"
  },
  {
    "renderMode": 2,
    "redirectTo": "/",
    "route": "/**"
  }
],
  entryPointToBrowserMapping: undefined,
  assets: {
    'index.csr.html': {size: 15290, hash: '91df548793c2d7e1d08b2ba5ffaa766e178abb607bd43dcf404ade3ddf44ce17', text: () => import('./assets-chunks/index_csr_html.mjs').then(m => m.default)},
    'index.server.html': {size: 961, hash: '3783ca5d8e593f8d29032ab7af3d6300ef3c05a1b489561c03950dd4d64d5841', text: () => import('./assets-chunks/index_server_html.mjs').then(m => m.default)},
    'seletor-login/index.html': {size: 26462, hash: 'dce37511d7319e69576b39dc690488c018d835c676c074babb7e88f00ceb1c04', text: () => import('./assets-chunks/seletor-login_index_html.mjs').then(m => m.default)},
    'cardapio/index.html': {size: 33332, hash: '0236a828e7e3756a9f5bf53299f9df664a48869b2a18786c1c3df750ddce9318', text: () => import('./assets-chunks/cardapio_index_html.mjs').then(m => m.default)},
    'login-cliente/index.html': {size: 26702, hash: '8d5868650cbfcf5852a829adb07f98723d7470d9f366029d6bcc490cc837d7fe', text: () => import('./assets-chunks/login-cliente_index_html.mjs').then(m => m.default)},
    'index.html': {size: 24350, hash: 'eb0ae0564910c0dfe94e0efaafe4ed13a4344fc7017320d9c75d3a3d719a52a0', text: () => import('./assets-chunks/index_html.mjs').then(m => m.default)},
    'pedidos/index.html': {size: 36646, hash: '5461aea5a344a5852c22d6ac39047482a81a15dd0f38180790de3ecea2fabf0e', text: () => import('./assets-chunks/pedidos_index_html.mjs').then(m => m.default)},
    'login-funcionario/index.html': {size: 26022, hash: '25e373a05d1231915aeb25bb73572eb74063d379cb139a3d977951ad78b0a07d', text: () => import('./assets-chunks/login-funcionario_index_html.mjs').then(m => m.default)},
    'styles-STOYL5A3.css': {size: 14934, hash: 'L7jzNu/oxgk', text: () => import('./assets-chunks/styles-STOYL5A3_css.mjs').then(m => m.default)}
  },
};
