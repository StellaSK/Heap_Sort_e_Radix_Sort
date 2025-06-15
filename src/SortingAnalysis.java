import java.util.*;
import java.io.*;

public class SortingAnalysis {
    static class Result {
        long time;
        long swaps;
        long iterations;
    }

    public static void main(String[] args) throws IOException {
        int[] sizes = {1000, 10000, 100000, 500000, 1000000};
        int numRuns = 5;

        // Configuração do arquivo CSV para saída
        File csvFile = new File("sorting_results.csv");
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("Algorithm,Size,Run,Time(ns),Swaps,Iterations");

            // Analisar Heap Sort
            for (int size : sizes) {
                for (int run = 0; run < numRuns; run++) {
                    int[] data = generateRandomArray(size, run);
                    int[] dataCopy = Arrays.copyOf(data, data.length);

                    Result heapResult = heapSort(data);
                    pw.println("HeapSort," + size + "," + run + "," +
                            heapResult.time + "," + heapResult.swaps + "," + heapResult.iterations);

                    // Verificar ordenação (opcional para grandes conjuntos)
                    if (!isSorted(data)) {
                        System.err.println("Heap Sort failed for size " + size + ", run " + run);
                    }

                    Result radixResult = radixSort(dataCopy);
                    pw.println("RadixSort," + size + "," + run + "," +
                            radixResult.time + "," + radixResult.swaps + "," + radixResult.iterations);

                    if (!isSorted(dataCopy)) {
                        System.err.println("Radix Sort failed for size " + size + ", run " + run);
                    }
                }
            }
        }
    }

    // Geração de array aleatório com seed controlada
    private static int[] generateRandomArray(int size, int seed) {
        int[] array = new int[size];
        Random rand = new Random(seed);
        for (int i = 0; i < size; i++) {
            array[i] = rand.nextInt(1000000); // 0 a 999.999
        }
        return array;
    }

    // Verificação se o array está ordenado
    private static boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    // Heap Sort
    public static Result heapSort(int[] array) {
        Result result = new Result();
        long startTime = System.nanoTime();
        int n = array.length;

        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i, result);
        }

        for (int i = n - 1; i > 0; i--) {
            swap(array, 0, i, result);
            heapify(array, i, 0, result);
        }

        result.time = System.nanoTime() - startTime;
        return result;
    }

    private static void heapify(int[] array, int n, int i, Result result) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n) {
            result.iterations++;
            if (array[left] > array[largest]) {
                largest = left;
            }
        }

        if (right < n) {
            result.iterations++;
            if (array[right] > array[largest]) {
                largest = right;
            }
        }

        if (largest != i) {
            swap(array, i, largest, result);
            heapify(array, n, largest, result);
        }
    }

    private static void swap(int[] array, int a, int b, Result result) {
        int temp = array[a];
        array[a] = array[b];
        array[b] = temp;
        result.swaps++;
    }

    // Radix Sort
    public static Result radixSort(int[] array) {
        Result result = new Result();
        long startTime = System.nanoTime();

        if (array.length == 0) {
            result.time = System.nanoTime() - startTime;
            return result;
        }

        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) max = array[i];
        }

        int exp = 1;
        while (max / exp > 0) {
            countingSort(array, exp, result);
            exp *= 10;
        }

        result.time = System.nanoTime() - startTime;
        return result;
    }

    private static void countingSort(int[] array, int exp, Result result) {
        int n = array.length;
        int[] output = new int[n];
        int[] count = new int[10];

        for (int i = 0; i < n; i++) {
            count[(array[i] / exp) % 10]++;
            result.iterations++;
        }

        for (int i = 1; i < 10; i++) {
            count[i] += count[i - 1];
            result.iterations++;
        }

        for (int i = n - 1; i >= 0; i--) {
            int index = count[(array[i] / exp) % 10] - 1;
            output[index] = array[i];
            count[(array[i] / exp) % 10]--;
            result.iterations++;
            result.swaps++; // Movimentação para output
        }

        for (int i = 0; i < n; i++) {
            array[i] = output[i];
            result.iterations++;
            result.swaps++; // Movimentação de volta para array
        }
    }
}