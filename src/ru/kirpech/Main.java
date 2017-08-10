package ru.kirpech;


public class Main {

    public static void main(String[] args) {
        // write your code here
        Population population = new Population(200, "/Users/user/IdeaProjects/shiftsPutting/src/ru/kirpech/input.txt");

        population.createPopulation();
        population.setNumberOfIterations(100);
        population.startGA();

    }
}
