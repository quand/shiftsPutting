package ru.kirpech;


import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // write your code here
        Scanner in = new Scanner(System.in);

        String filePath = "/Users/user/IdeaProjects/shiftsPutting/src/ru/kirpech/input.txt";
        System.out.println("Введите полный путь к файлу: ");
        if (in.hasNext()) {
            filePath = in.next();
        }
        Population population = new Population(200, filePath);

        population.createPopulation();
        population.setNumberOfIterations(5000);
        population.startGA();

//best score 184 with 200 members and 5000 iterations
    }
}
