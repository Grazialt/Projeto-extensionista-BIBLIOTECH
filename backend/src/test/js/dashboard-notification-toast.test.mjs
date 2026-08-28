import assert from 'node:assert/strict';
import fs from 'node:fs';

const dashboardPath = new URL('../../main/resources/static/dashboard.html', import.meta.url);
const html = fs.readFileSync(dashboardPath, 'utf8');

assert.match(html, /id="notificationToastContainer"/, 'dashboard deve ter um container de toast de notificações');
assert.match(html, /function\s+mostrarToastNotificacao\s*\(/, 'dashboard deve ter função para exibir toast');
assert.match(html, /function\s+tocarSomNotificacao\s*\(/, 'dashboard deve ter função para tocar som');
assert.match(html, /function\s+processarNovasNotificacoes\s*\(/, 'dashboard deve detectar apenas notificações novas');
assert.match(html, /bibliotech_notification_last_seen_/, 'dashboard deve persistir o último ID visto para evitar repetição após refresh');
assert.match(html, /setInterval\(carregarNotificacoes,\s*10000\)/, 'dashboard deve consultar novas notificações a cada 10 segundos');

console.log('dashboard notification toast test: PASS');
