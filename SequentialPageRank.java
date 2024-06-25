import java.io.*;
import java.util.*;

public class SequentialPageRank {
  private final int numNodes;
  private final List<Integer>[] adjList;
  private final double[] currentPageranks;
  private final double[] newPageranks;
  private final double dampingFactor;
  private final double convergenceThreshold;

  @SuppressWarnings("unchecked")
  public SequentialPageRank(String filePath, double dampingFactor, double convergenceThreshold) throws IOException {
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
    this.currentPageranks = new double[numNodes];
    this.newPageranks = new double[numNodes];
    Arrays.fill(this.currentPageranks, 1.0 / numNodes);
    Arrays.fill(this.newPageranks, 1.0 / numNodes);
  }

  public void computePageRank() {
    boolean converged = false;
    while (!converged) {
      for (int i = 0; i < numNodes; i++) {
        double sum = 0.0;
        for (int j = 0; j < numNodes; j++) {
          if (adjList[j].contains(i)) {
            sum += currentPageranks[j] / adjList[j].size();
          }
        }
        newPageranks[i] = (1 - dampingFactor) / numNodes + dampingFactor * sum;
      }

      converged = checkConvergence();
      if (!converged) {
        System.arraycopy(newPageranks, 0, currentPageranks, 0, numNodes);
      }
    }
  }

  private boolean checkConvergence() {
    double sumDifference = 0.0;
    for (int i = 0; i < numNodes; i++) {
      sumDifference += Math.abs(currentPageranks[i] - newPageranks[i]);
    }
    return sumDifference < convergenceThreshold;
  }

  public static void main(String[] args) throws InterruptedException, IOException {
    if (args.length != 3) {
      System.out.println("Usage: java SequentialPageRank <inputFile> <dampingFactor> <convergenceThreshold>");
      return;
    }

    String filePath = args[0];
    double dampingFactor = Double.parseDouble(args[1]);
    double convergenceThreshold = Double.parseDouble(args[2]);

    SequentialPageRank pagerank = new SequentialPageRank(filePath, dampingFactor, convergenceThreshold);
    long startTime = System.currentTimeMillis();
    pagerank.computePageRank();
    long endTime = System.currentTimeMillis();
    long executionTime = endTime - startTime;

    // Exibir resultados
    System.out.println("Pageranks:");
    for (int i = 0; i < pagerank.numNodes; i++) {
      System.out.println("Page " + i + ": " + pagerank.currentPageranks[i]);
    }
    System.out.println("Tempo de execução: " + executionTime + " milissegundos");
  }
}
