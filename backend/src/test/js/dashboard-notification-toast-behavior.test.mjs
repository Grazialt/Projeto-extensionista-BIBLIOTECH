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
  Math
};
vm.createContext(context);
vm.runInContext(`${match[0]}; this.processarNovasNotificacoes = processarNovasNotificacoes;`, context);

const usuarioId = 7;
context.processarNovasNotificacoes([{id: 10}, {id: 9}], usuarioId);
assert.deepEqual(toasts, [], 'primeira carga deve apenas criar referência, sem toast');
assert.equal(sons, 0, 'primeira carga não deve tocar som');

context.processarNovasNotificacoes([{id: 11}, {id: 10}], usuarioId);
assert.deepEqual(toasts, [11], 'novo ID deve gerar toast');
assert.equal(sons, 1, 'lote novo deve tocar som uma vez');

context.processarNovasNotificacoes([{id: 11}, {id: 10}], usuarioId);
assert.deepEqual(toasts, [11], 'mesma notificação não deve repetir toast');
assert.equal(sons, 1, 'mesma notificação não deve repetir som');

context.processarNovasNotificacoes([{id: 13}, {id: 12}, {id: 11}], usuarioId);
assert.deepEqual(toasts, [11, 12, 13], 'novos IDs devem ser exibidos em ordem');
assert.equal(sons, 2, 'cada lote novo deve tocar um único som');

console.log('dashboard notification toast behavior test: PASS');
