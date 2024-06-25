import subprocess
import pandas as pd
import numpy as np
import time

# Configurações dos testes
test_cases = [
    {"vertices": 100, "arestas": 200},
    {"vertices": 500, "arestas": 1000},
    {"vertices": 1000, "arestas": 2000}
]

num_threads_list = [2, 4, 8]  # Número de threads variado
num_repeats = 5  # Quantidade de vezes que cada teste será repetido para calcular a média

# Função para executar o PageRank sequencial e calcular o tempo médio
def run_sequential_pagerank(vertices, arestas):
    tempos_execucao = []
    for _ in range(num_repeats):
        comando = ["java", "SequentialPageRank", str(vertices), str(arestas)]
        inicio = time.time()
        resultado = subprocess.run(comando, capture_output=True, text=True)
        fim = time.time()
        tempo_execucao = fim - inicio
        tempos_execucao.append(tempo_execucao)
    
    tempo_medio = np.mean(tempos_execucao)
    return tempo_medio

# Função para executar o PageRank paralelo com um número específico de threads e calcular o tempo médio
def run_parallel_pagerank(vertices, arestas, threads):
    tempos_execucao = []
    for _ in range(num_repeats):
        comando = ["java", "ParallelPageRank", str(vertices), str(arestas), str(threads)]
        inicio = time.time()
        resultado = subprocess.run(comando, capture_output=True, text=True)
        fim = time.time()
        tempo_execucao = fim - inicio
        tempos_execucao.append(tempo_execucao)
    
    tempo_medio = np.mean(tempos_execucao)
    return tempo_medio

# Lista para armazenar os resultados
resultados = []

# Executa os testes sequencialmente
for case in test_cases:
    vertices = case["vertices"]
    arestas = case["arestas"]
    
    tempo_sequencial = run_sequential_pagerank(vertices, arestas)
    
    for threads in num_threads_list:
        tempo_paralelo = run_parallel_pagerank(vertices, arestas, threads)
        
        # Calcular a aceleração
        aceleracao = tempo_sequencial / tempo_paralelo if threads > 1 else "N/A"
        
        resultados.append({
            "Tamanho do Grafo": f"{vertices} vértices, {arestas} arestas",
            "Número de Threads": threads,
            "Tempo Médio Sequencial (s)": tempo_sequencial,
            "Tempo Médio Paralelo (s)": tempo_paralelo,
            "Aceleração": aceleracao
        })

# Converter resultados para DataFrame do pandas
df_resultados = pd.DataFrame(resultados)

# Escrever resultados em uma planilha Excel
nome_arquivo = "comparacao_pagerank_threads.xlsx"
df_resultados.to_excel(nome_arquivo, index=False)

print(f"Resultados salvos em {nome_arquivo}")
