import java.io.*;
import java.util.*;

public class RandomGraphGenerator {
  public static void main(String[] args) throws IOException {
    // Verifica se o número correto de argumentos foi passado
    if (args.length != 3) {
      System.out.println("Usage: java RandomGraphGenerator <numVertices> <numEdges> <outputFile>");
      return;
    }

    // Lê os argumentos de entrada
    int numVertices = Integer.parseInt(args[0]);
    int numEdges = Integer.parseInt(args[1]);
    String outputFile = args[2];

    // Gera um grafo direcionado aleatório
    List<Integer>[] adjList = generateRandomDirectedGraph(numVertices, numEdges);

    // Escreve o grafo gerado em um arquivo
    writeGraphToFile(adjList, outputFile);
  }

  @SuppressWarnings("unchecked")
  public static List<Integer>[] generateRandomDirectedGraph(int numVertices, int numEdges) {
    // Inicializa a lista de adjacência
    List<Integer>[] adjList = new List[numVertices];
    for (int i = 0; i < numVertices; i++) {
      adjList[i] = new ArrayList<>();
    }

    Random random = new Random();
    Set<String> edges = new HashSet<>();

    // Gera arestas aleatórias até atingir o número desejado de arestas
    while (edges.size() < numEdges) {
      int u = random.nextInt(numVertices);
      int v = random.nextInt(numVertices);

      // Certifica-se de que não haja arestas auto-referenciadas e duplicadas
      if (u != v && !edges.contains(u + "-" + v)) {
        adjList[u].add(v);
        edges.add(u + "-" + v);
      }
    }

    return adjList;
  }

  public static void writeGraphToFile(List<Integer>[] adjList, String outputFile) throws IOException {
    // Escreve o grafo em um arquivo binário
    DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFile));
    dos.writeInt(adjList.length); // Escreve o número de vértices
    for (List<Integer> neighbors : adjList) {
      dos.writeInt(neighbors.size()); // Escreve o número de arestas para cada vértice
      for (int neighbor : neighbors) {
        dos.writeInt(neighbor); // Escreve os vértices adjacentes
      }
    }
    dos.close();
  }
}
