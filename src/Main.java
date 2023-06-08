import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    // Константи та параметри алгоритму
    private static final int NUM_LESSONS = 23; // Кількість уроків на тиждень
    private static final int NUM_SUBJECTS = 6; // Кількість різних уроків
    private static final int NUM_TEACHERS = 4; // Кількість різних вчителів
    private static final int NUM_CLASSES = 3; // Кількість класів
    private static final int MAX_LESSONS_PER_DAY = 5; // Максимальна кількість уроків на день
    private static final int NUM_SPECIAL_ROOMS = 1; // Кількість спеціалізованих приміщень

    private static final int POPULATION_SIZE = 50; // Розмір початкової популяції
    private static final int MAX_GENERATIONS = 100; // Максимальна кількість поколінь
    private static final double CROSSOVER_RATE = 0.8; // Ймовірність схрещування
    private static final double MUTATION_RATE = 0.2; // Ймовірність мутації

    // Списки даних
    private static final String[] lessons = new String[NUM_SUBJECTS];
    private static final String[] teachers = new String[NUM_TEACHERS];
    private static final String[] classes = new String[NUM_CLASSES];
    private static final String[] rooms = new String[NUM_CLASSES + NUM_SPECIAL_ROOMS];

    static {
        // Ініціалізація списків даних
        for (int i = 0; i < NUM_SUBJECTS; i++) {
            lessons[i] = "Урок " + i;
        }
        for (int i = 0; i < NUM_TEACHERS; i++) {
            teachers[i] = "Вчитель " + i;
        }
        for (int i = 0; i < NUM_CLASSES; i++) {
            classes[i] = "Клас " + i;
        }
        for (int i = 0; i < NUM_CLASSES + NUM_SPECIAL_ROOMS; i++) {
            rooms[i] = "Кімната " + i;
        }
    }

    // Генерація випадкового розкладу занять
    private static List<List<String>> generateSchedule() {
        List<List<String>> schedule = new ArrayList<>();
        Random random = new Random();
        for (int day = 0; day < 5; day++) { // 5 робочих днів
            List<String> dailySchedule = new ArrayList<>();
            for (int i = 0; i < MAX_LESSONS_PER_DAY; i++) {
                if (dailySchedule.size() < NUM_LESSONS) {
                    String lesson = lessons[random.nextInt(NUM_SUBJECTS)];
                    String teacher = teachers[random.nextInt(NUM_TEACHERS)];
                    String classroom = rooms[random.nextInt(rooms.length)];
                    dailySchedule.add(lesson + ", " + teacher + ", " + classroom);
                } else {
                    dailySchedule.add(null); // Вікно (пропуск уроку)
                }
            }
            schedule.add(dailySchedule);
        }
        return schedule;
    }

    // Обчислення фітнес-функції (оцінки якості розкладу)
    private static int calculateFitness(List<List<String>> schedule) {
        int fitness = 0;
        for (List<String> day : schedule) {
            for (String lesson : day) {
                if (lesson != null) {
                    fitness++; // Збільшуємо фітнес за кожний заповнений урок
                }
            }
        }
        return fitness;
    }

    // Схрещування (одноточковий кросовер)
    private static List<List<String>> crossover(List<List<String>> parent1, List<List<String>> parent2) {
        Random random = new Random();
        int crossoverPoint = random.nextInt(NUM_LESSONS - 1) + 1;
        List<List<String>> child = new ArrayList<>();
        for (int i = 0; i < parent1.size(); i++) {
            if (i < crossoverPoint) {
                child.add(new ArrayList<>(parent1.get(i)));
            } else {
                child.add(new ArrayList<>(parent2.get(i)));
            }
        }
        return child;
    }

    // Мутація (заміна випадкового уроку)
    private static List<List<String>> mutate(List<List<String>> schedule) {
        Random random = new Random();
        int day = random.nextInt(5);
        int lesson = random.nextInt(MAX_LESSONS_PER_DAY);
        String newLesson = lessons[random.nextInt(NUM_SUBJECTS)];
        String newTeacher = teachers[random.nextInt(NUM_TEACHERS)];
        String newClassroom = rooms[random.nextInt(rooms.length)];
        schedule.get(day).set(lesson, newLesson + ", " + newTeacher + ", " + newClassroom);
        return schedule;
    }

    // Генетичний алгоритм оптимізації розкладу занять
    private static List<List<String>> geneticAlgorithm() {
        // Ініціалізація початкової популяції
        List<List<List<String>>> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(generateSchedule());
        }

        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            // Оцінка фітнес-функції для кожного розкладу
            int[] fitnessScores = new int[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                fitnessScores[i] = calculateFitness(population.get(i));
            }

            // Вибір найкращих розкладів для схрещування
            List<List<List<String>>> selectedParents = new ArrayList<>();
            for (int i = 0; i < POPULATION_SIZE / 2; i++) {
                int parent1Index = rouletteWheelSelection(fitnessScores);
                int parent2Index = rouletteWheelSelection(fitnessScores);
                selectedParents.add(population.get(parent1Index));
                selectedParents.add(population.get(parent2Index));
            }

            // Створення нової популяції
            List<List<List<String>>> newPopulation = new ArrayList<>();
            for (int i = 0; i < selectedParents.size(); i += 2) {
                List<List<String>> parent1 = selectedParents.get(i);
                List<List<String>> parent2 = selectedParents.get(i + 1);
                List<List<String>> child1;
                List<List<String>> child2;
                if (Math.random() < CROSSOVER_RATE) {
                    child1 = crossover(parent1, parent2);
                    child2 = crossover(parent2, parent1);
                } else {
                    child1 = new ArrayList<>(parent1);
                    child2 = new ArrayList<>(parent2);
                }
                newPopulation.add(child1);
                newPopulation.add(child2);
            }

            // Мутація в новій популяції
            for (int i = 0; i < newPopulation.size(); i++) {
                if (Math.random() < MUTATION_RATE) {
                    newPopulation.set(i, mutate(newPopulation.get(i)));
                }
            }

            // Заміна старої популяції новою
            population = newPopulation;
        }

        // Вибір найкращого розкладу
        List<List<String>> bestSchedule = population.get(0);
        int bestFitness = calculateFitness(bestSchedule);

        for (List<List<String>> schedule : population) {
            int fitness = calculateFitness(schedule);
            if (fitness > bestFitness) {
                bestSchedule = schedule;
                bestFitness = fitness;
            }
        }

        return bestSchedule;
    }

    // Вибір батьків методом рулеткового колеса
    private static int rouletteWheelSelection(int[] fitnessScores) {
        int sum = 0;
        for (int score : fitnessScores) {
            sum += score;
        }

        Random random = new Random();
        double randomValue = random.nextDouble() * sum;
        double partialSum = 0;
        for (int i = 0; i < fitnessScores.length; i++) {
            partialSum += fitnessScores[i];
            if (partialSum >= randomValue) {
                return i;
            }
        }

        return fitnessScores.length - 1;
    }

    // Виведення результатів
    private static void printSchedule(List<List<String>> schedule, int fitness) {
        System.out.println("Найкращий розклад занять:");
        for (int day = 0; day < schedule.size(); day++) {
            System.out.println("\nДень " + (day + 1));
            List<String> dailySchedule = schedule.get(day);
            for (String lesson : dailySchedule) {
                if (lesson != null) {
                    System.out.println(lesson);
                } else {
                    System.out.println("Вікно (пропуск уроку)");
                }
                System.out.println();
            }
        }
        System.out.println("Оцінка якості розкладу: " + fitness);
    }

    public static void main(String[] args) {
        List<List<String>> bestSchedule = geneticAlgorithm();
        int bestFitness = calculateFitness(bestSchedule);
        printSchedule(bestSchedule, bestFitness);
    }
}
