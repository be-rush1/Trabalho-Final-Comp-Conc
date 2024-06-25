import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ParallelPageRank {
    private final int numNodes; // Número de nós (páginas) no grafo
    private final List<Integer>[] adjList; // Lista de adjacência do grafo
    private final double[] currentPageranks; // Array para armazenar os PageRanks atuais
    private final double[] newPageranks; // Array para armazenar os novos PageRanks calculados
    private final double dampingFactor; // Fator de amortecimento (damping factor)
    private final double convergenceThreshold; // Limite de convergência
    private final int numThreads; // Número de threads a serem utilizadas
    private ExecutorService executor; // Executor para gerenciar as threads

    @SuppressWarnings("unchecked")
    public ParallelPageRank(String filePath, double dampingFactor, double convergenceThreshold, int numThreads)
            throws IOException {
        // Lê o grafo do arquivo binário
        DataInputStream dis = new DataInputStream(new FileInputStream(filePath));
        this.numNodes = dis.readInt();
        this.adjList = new List[numNodes];
        for (int i = 0; i < numNodes; i++) {
            adjList[i] = new ArrayList<>();
            int numEdges = dis.readInt();
            for (int j = 0; j < numEdges; j++) {
                adjList[i].add(dis.readInt());
            }
        }
        dis.close();

        this.dampingFactor = dampingFactor;
        this.convergenceThreshold = convergenceThreshold;
        this.numThreads = numThreads;
        this.currentPageranks = new double[numNodes];
        this.newPageranks = new double[numNodes];
        Arrays.fill(this.currentPageranks, 1.0 / numNodes); // Inicializa os PageRanks atuais
        Arrays.fill(this.newPageranks, 1.0 / numNodes); // Inicializa os novos PageRanks
    }

    public void computePageRank() throws InterruptedException {
        executor = Executors.newFixedThreadPool(numThreads); // Cria um pool de threads com o número especificado

        boolean converged = false;
        while (!converged) {
            List<Future<?>> futures = new ArrayList<>();
            // Divide a tarefa entre as threads
            for (int i = 0; i < numThreads; i++) {
                int start = i * numNodes / numThreads;
                int end = (i + 1) * numNodes / numThreads;
                futures.add(executor.submit(new PageRankTask(start, end)));
            }
            // Espera todas as threads terminarem
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // Verifica se os valores de PageRank convergiram
            converged = checkConvergence();
            if (!converged) {
                // Atualiza os PageRanks atuais com os novos valores calculados
                System.arraycopy(newPageranks, 0, currentPageranks, 0, numNodes);
            }
        }
        executor.shutdown(); // Desliga o executor
        executor.awaitTermination(60, TimeUnit.SECONDS); // Espera o término das threads
    }

    private boolean checkConvergence() {
        double sumDifference = 0.0;
        for (int i = 0; i < numNodes; i++) {
            sumDifference += Math.abs(currentPageranks[i] - newPageranks[i]); // Soma as diferenças absolutas
        }
        return sumDifference < convergenceThreshold; // Retorna true se a soma das diferenças for menor que o limite de
                                                     // convergência
    }

    private class PageRankTask implements Runnable {
        private final int start; // Índice inicial do intervalo de nós a ser processado pela thread
        private final int end; // Índice final do intervalo de nós a ser processado pela thread

        public PageRankTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                double sum = 0.0;
                // Calcula a soma dos PageRanks dos nós que apontam para o nó atual
                for (int j = 0; j < numNodes; j++) {
                    if (adjList[j].contains(i)) {
                        sum += currentPageranks[j] / adjList[j].size();
                    }
                }
                // Calcula o novo PageRank para o nó atual
                newPageranks[i] = (1 - dampingFactor) / numNodes + dampingFactor * sum;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length != 4) {
            System.out.println(
                    "Usage: java ParallelPageRank <inputFile> <dampingFactor> <convergenceThreshold> <numThreads>");
            return;
        }

        String filePath = args[0]; // Caminho do arquivo de entrada
        double dampingFactor = Double.parseDouble(args[1]); // Fator de amortecimento
        double convergenceThreshold = Double.parseDouble(args[2]); // Limite de convergência
        int numThreads = Integer.parseInt(args[3]); // Número de threads

        // Cria uma instância do PageRank paralelo
        ParallelPageRank pagerank = new ParallelPageRank(filePath, dampingFactor, convergenceThreshold, numThreads);
        long startTime = System.currentTimeMillis(); // Marca o tempo de início
        pagerank.computePageRank(); // Executa o algoritmo PageRank
        long endTime = System.currentTimeMillis(); // Marca o tempo de término
        long executionTime = endTime - startTime; // Calcula o tempo de execução

        // Exibe os resultados
        System.out.println("Pageranks:");
        for (int i = 0; i < pagerank.numNodes; i++) {
            System.out.println("Page " + i + ": " + pagerank.currentPageranks[i]);
        }
        System.out.println("Tempo de execução: " + executionTime + " milissegundos");
    }
}
