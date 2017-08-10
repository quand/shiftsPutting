package ru.kirpech;


public class Main {

    public static void main(String[] args) {
        // write your code here
        Population population = new Population(100, "/Users/user/IdeaProjects/shiftsPutting/src/ru/kirpech/input.txt");

        population.createPopulation();
        population.setNumberOfIterations(500);
        population.startGA();
    }
}
