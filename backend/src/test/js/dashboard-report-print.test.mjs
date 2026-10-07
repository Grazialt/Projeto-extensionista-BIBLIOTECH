import assert from 'node:assert/strict';
import fs from 'node:fs';

const dashboardPath = new URL('../../main/resources/static/dashboard.html', import.meta.url);
const html = fs.readFileSync(dashboardPath, 'utf8');
const reportIds = [
  'indicadores',
  'evolucao',
  'ranking-alunos',
  'ranking-turmas',
  'detalhe-turma',
  'ranking-livros',
  'emprestimos-ativos'
];

for (const id of reportIds) {
  assert.match(html, new RegExp(`data-print-report="${id}"`), `relatório ${id} deve ser imprimível`);
  assert.match(html, new RegExp(`imprimirRelatorio\\('${id}'\\)`), `relatório ${id} deve ter botão de impressão`);
}

assert.match(html, /function\s+imprimirRelatorio\s*\(/, 'dashboard deve definir a função de impressão');
assert.doesNotMatch(html, /window\.open\(/, 'impressão não deve abrir outra aba ou janela');
assert.match(html, /document\.body\.appendChild\(documentoImpressao\)/, 'relatório deve ser montado temporariamente na página atual');
assert.match(html, /window\.print\(\)/, 'impressão deve usar o diálogo padrão na página atual');
assert.match(html, /window\.addEventListener\('afterprint', finalizarImpressao/, 'conteúdo temporário deve ser limpo após imprimir');
assert.match(html, /documentoImpressao\.remove\(\)/, 'relatório temporário deve ser removido ao terminar');
assert.match(html, /report-page-header/, 'documento deve ter cabeçalho próprio');
assert.match(html, /report-brand/, 'cabeçalho deve identificar a biblioteca');
assert.match(html, /Emitido em/, 'relatório deve incluir data de emissão');
assert.match(html, /@page \{ size: A4;/, 'impressão deve usar página A4');
assert.match(html, /body\.printing-report > \*\s*\{\s*display:\s*none !important;/, 'dashboard deve ser ocultado somente na versão impressa');
assert.match(html, /display:\s*table-header-group/, 'títulos de tabelas devem repetir em novas páginas');
assert.match(html, /report-class-name/, 'relatório de turma deve identificar a turma selecionada');
assert.match(html, /Resumo de indicadores/, 'relatório de indicadores deve ter título próprio');
assert.match(html, /\.bar-chart\s*\{[^}]*grid-template-columns:\s*repeat\(6,\s*minmax\(0,\s*1fr\)\)/s, 'gráfico deve manter cada período alinhado em uma grade fixa');
assert.match(html, /\.bar-track\s*\{[^}]*align-items:flex-end/s, 'barras devem compartilhar a mesma linha de base');
const barLabelStyles = html.match(/\.bar-label\s*\{([^}]*)\}/)?.[1] || '';
assert.ok(barLabelStyles.includes('text-align:center'), 'rótulos dos períodos devem ficar centralizados');
assert.doesNotMatch(barLabelStyles, /rotate\(/, 'rótulos dos gráficos não devem ficar inclinados');
assert.ok((html.match(/renderGraficoBarras\(/g) || []).length >= 3, 'gráfico geral e de turma devem usar o mesmo renderizador');
assert.match(html, /#reportPrintDocument \.bar-chart\s*\{[^}]*break-inside:\s*avoid-page/s, 'gráficos devem ser mantidos juntos na impressão');

console.log('dashboard report print test: PASS');
