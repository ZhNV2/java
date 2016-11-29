package sp;

import java.io.FileReader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        return paths
                .stream()
                .filter(path-> {
                    try {
                        FileReader fileReader = new FileReader(path);
                        StringBuilder stringBuilder = new StringBuilder();
                        int c = 0;
                        while ((c = fileReader.read()) != -1) {
                            stringBuilder.append((char)c);
                        }
                        String text = stringBuilder.toString();
                        fileReader.close();
                        return text.contains(sequence);
                    } catch (Throwable e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        final int ITS = 1000000;
        final Random random = new Random();
        final Double radius = 0.5;
        return IntStream
                .iterate(1, n -> n + 1)
                .limit(ITS)
                .filter(i -> {
                    double x = random.nextDouble() * 2 * radius - radius;
                    double y = random.nextDouble() * 2 * radius - radius;
                    return x * x + y * y < radius * radius;
                })
                .count() / (ITS + 0.);
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .max(Comparator.comparingInt(author -> {
                    List<String> comps = compositions.getOrDefault(author, Collections.emptyList());
                    int len = 0;
                    if (comps != null) {
                        for (String text : comps) {
                            len += text.length();
                        }
                    }
                    return len;}))
                .orElse(null);
    }
    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders
                .stream()
                .filter(a -> a != null)
                .flatMap(a->a.entrySet().stream())
                .map(Map.Entry::getKey)
                .distinct()
                .filter(a -> a != null)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(a -> a == null ? 0 :
                            orders
                                .stream()
                                .mapToInt(m -> {
                                    if (m == null) return 0;
                                    if (m.getOrDefault(a, 0) == null) return 0;
                                    return  m.getOrDefault(a, 0);
                                })
                                .sum())));
    }
}
