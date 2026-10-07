import assert from 'node:assert/strict';
import fs from 'node:fs';
import vm from 'node:vm';

const dashboardPath = new URL('../../main/resources/static/dashboard.html', import.meta.url);
const html = fs.readFileSync(dashboardPath, 'utf8');
const match = html.match(/function\s+processarNovasNotificacoes\s*\([^)]*\)\s*\{[\s\S]*?\n        \}/);
assert.ok(match, 'função processarNovasNotificacoes deve existir');

const storage = new Map();
const toasts = [];
let sons = 0;
const context = {
  localStorage: {
    getItem: key => storage.has(key) ? storage.get(key) : null,
    setItem: (key, value) => storage.set(key, String(value))
  },
  mostrarToastNotificacao: n => toasts.push(n.id),
  tocarSomNotificacao: () => { sons += 1; },
  Array,
  Number,
  Math,
  Set,
  JSON
};
vm.createContext(context);
vm.runInContext(`${match[0]}; this.processarNovasNotificacoes = processarNovasNotificacoes;`, context);

const usuarioId = 7;
context.processarNovasNotificacoes([
  {id: 10, tipo: 'EMPRESTIMO', lida: false},
  {id: 9, tipo: 'ATRASO', lida: false}
], usuarioId);
assert.deepEqual(toasts, [9], 'primeira carga deve avisar atraso não lido sem exibir histórico comum');
assert.equal(sons, 1, 'atraso não lido deve tocar som na primeira carga');

context.processarNovasNotificacoes([
  {id: 11, tipo: 'EMPRESTIMO', lida: false},
  {id: 10, tipo: 'EMPRESTIMO', lida: false},
  {id: 9, tipo: 'ATRASO', lida: false}
], usuarioId);
assert.deepEqual(toasts, [9, 11], 'novo ID deve gerar toast e o atraso não deve repetir');
assert.equal(sons, 2, 'lote novo deve tocar som uma vez');

context.processarNovasNotificacoes([
  {id: 11, tipo: 'EMPRESTIMO', lida: false},
  {id: 10, tipo: 'EMPRESTIMO', lida: false},
  {id: 9, tipo: 'ATRASO', lida: false}
], usuarioId);
assert.deepEqual(toasts, [9, 11], 'notificações já exibidas não devem repetir toast');
assert.equal(sons, 2, 'notificações já exibidas não devem repetir som');

context.processarNovasNotificacoes([
  {id: 13, tipo: 'ATRASO', lida: false},
  {id: 12, tipo: 'EMPRESTIMO', lida: false},
  {id: 11, tipo: 'EMPRESTIMO', lida: false},
  {id: 9, tipo: 'ATRASO', lida: false}
], usuarioId);
assert.deepEqual(toasts, [9, 11, 12, 13], 'atraso não lido posterior ao último ID também deve aparecer');
assert.equal(sons, 3, 'cada lote novo deve tocar um único som');

console.log('dashboard notification toast behavior test: PASS');
