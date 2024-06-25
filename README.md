# Implementação e Avaliação de Desempenho do PageRank

Este repositório contém a implementação e avaliação de desempenho do algoritmo PageRank utilizando abordagens sequencial e paralela. A implementação paralela aproveita as capacidades de multithreading do Java para melhorar a eficiência computacional. Inclui também um gerador de grafos direcionados aleatórios e um script Python para automatizar testes e coletar resultados.

## Índice

- [Descrição do Algoritmo PageRank](#descrição-do-algoritmo-pagerank)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Configuração](#configuração)
- [Execução do Código](#execução-do-código)
  - [Geração de Grafo](#geração-de-grafo)
  - [Cálculo do PageRank](#cálculo-do-pagerank)
  - [Avaliação de Desempenho](#avaliação-de-desempenho)
- [Resultados](#resultados)
- [Referências](#referências)

## Descrição do Algoritmo PageRank

O PageRank é um algoritmo utilizado para classificar páginas da web baseado em sua importância. Foi desenvolvido pelo Google para ajudar a ordenar os resultados de busca de acordo com a relevância das páginas. 

- **Inicialização**: Todas as páginas recebem um valor inicial de PageRank.
- **Iteração**: Em cada iteração, o PageRank de cada página é atualizado com base nos PageRanks das páginas que apontam para ela.
- **Convergência**: O processo continua até que as mudanças nos valores de PageRank entre as iterações sejam menores que um limite pré-definido.

### Fórmula Matemática

\[ PR(P_i) = \frac{1 - d}{N} + d \left( \sum_{P_j \in M(P_i)} \frac{PR(P_j)}{L(P_j)} \right) \]

Onde:
- \( PR(P_i) \) é o PageRank da página \( P_i \).
- \( d \) é o fator de amortecimento (geralmente definido como 0.85).
- \( N \) é o número total de páginas.
- \( M(P_i) \) é o conjunto de páginas que apontam para \( P_i \).
- \( L(P_j) \) é o número de links de saída na página \( P_j \).

## Estrutura do Projeto

├── RandomGraphGenerator.java # Código Java para gerar grafos direcionados aleatórios
├── SequentialPageRank.java # Código Java para computação sequencial do PageRank
├── ParallelPageRank.java # Código Java para computação paralela do PageRank
├── run_tests.py # Script Python para executar testes e coletar resultados
├── resultados_pagerank.xlsx # Arquivo de saída com os resultados dos testes
└── README.md # Este arquivo README
