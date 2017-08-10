package ru.kirpech;

import java.io.File;
import java.io.IOException;
import java.util.*;

class Chromosome {
    private final static int D_SCORE = 3;
    private final static int N_SCORE = 3;
    private final static int R_SCORE = 4;
    private final static int UNASSIGNED_SCORE = -20;
    private final static int MORE_NNN_SCORE = -10;
    private final static int LONG_REST = 5;
    private final static int X_SCORE = -10000;
    private final static int N_D_SCORE = -30;
    private final static int MORE_N_SCORE = -8;
    private static ArrayList<ArrayList<Character>> origin;
    private static String fin;
    private double proc;
    private int score;
    private ArrayList<ArrayList<Character>> matrix = new ArrayList<>();
    private int[][][] shifts;

    Chromosome() {

        score = 0;
    }

    static void selection() {

    }

    /*
        нужен метод для скрещивания в котором
        проходит биекция значений на числовую прямую от 1
        находится вероятность того что попадет в данную секцию(выиграет данное решение)
        сортируем популяцию по значению вероятности
        генерируем 2 значения от 0 до 1
        бинарным поиском находим родителей для скрещивания
        скрестили, получии два новых и добавили их в популяцию
        */
    static void setOrigin(String fin) {
        Chromosome.fin = fin;
        Chromosome.origin = read();
    }

    /*static void annealing() {
        double temperature = 1, minTemp = 1e-30, step = 0.001;
        Chromosome state = new Chromosome();
        state.init();
        print(state);
        int stateEnergy = state.calculateScore();
        System.out.print(state.calculateScore());
        System.out.println();
        int i = 0, numIter = 1000000;
        Chromosome candidate = new Chromosome();
        candidate.init();
        int candidateEnergy;
        while (temperature > minTemp && i < numIter) {
            candidate.mutate();
            //print(candidate);
            candidateEnergy = candidate.calculateScore();
            if (candidateEnergy > stateEnergy) {
                state.copy(candidate);
                stateEnergy = candidateEnergy;
            } else if (checkProbability(candidateEnergy - stateEnergy, temperature)) {
                state.copy(candidate);
                stateEnergy = candidateEnergy;
            }
            temperature /= Math.pow(i, (1 / 10.0)); //сверхбыстрый отжиг
            //temperature = temperature * (1 - step);
            if (i % 10000 == 0) {
                print(state);
                System.out.println(stateEnergy);
            }
            if (temperature < minTemp) {
                break;
            }
            i++;
        }
        System.out.print(state.calculateScore());

    }
    */
    static void print(Chromosome state) {
        for (ArrayList row : state.matrix) {
            int i = 1;
            for (Object col : row) {
                if (((ArrayList) row.get(0)).size() == 1 && i == 1) {
                    System.out.print(col + "    ");
                    i--;
                } else
                    System.out.print(col + " ");
            }
            System.out.println();
        }
        System.out.println();
        for (int[][] route : state.shifts) {
            for (int[] time : route) {
                for (int day : time) {
                    System.out.print(day + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private static boolean checkProbability(int deltaE, double temperature) {
        double p = Math.exp(-deltaE / temperature);
        double val = RandomUtils.nextDouble();
        return val <= p;
    }

    private static ArrayList<ArrayList<Character>> read() {
        ArrayList<ArrayList<Character>> matrix = new ArrayList<>();
        try {
            List<String> list = new ArrayList<>();
            Scanner in = new Scanner(new File(fin));
            while (in.hasNextLine()) {
                list.add(in.nextLine());
            }
            in.close();
            list.remove(0);
            for (String aList : list) {
                ArrayList worker = new ArrayList();
                StringTokenizer st = new StringTokenizer(aList, "|", false);
                int k = 0;
                ArrayList line = new ArrayList();
                while (st.hasMoreTokens()) {
                    if (k == 2) {
                        String str = st.nextToken();
                        for (int j = 0; j < 3; j++)
                            if (String.valueOf(str).charAt(j) != '0')
                                line.add(String.valueOf(str).charAt(j));
                        worker.add(line);
                        k++;
                    }
                    worker.add((String.valueOf(st.nextToken())));
                    k++;
                }
                worker.remove(0);
                worker.remove(0);
                matrix.add(worker);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }

    static ArrayList<Chromosome> crossing(Chromosome a, Chromosome b) {
        Chromosome childA = new Chromosome();
        childA.copy(a);
        Chromosome childB = new Chromosome();
        childB.copy(b);
        int pointA = 0, pointB = 0;

        while (pointA >= pointB) {
            pointA = RandomUtils.nextInt(1, 15);
            pointB = RandomUtils.nextInt(1, 15);
        }

        /*for (ArrayList<Character> worker : a.matrix) {
            ArrayList<Character> chB = childB.matrix.get(a.matrix.indexOf(worker));
            for (int i = pointA; i < pointB; i++)
                chB.set(i, worker.get(i));
        }*/

        for (ArrayList<Character> worker : a.matrix) {
            ArrayList<Character> chB = childB.matrix.get(a.matrix.indexOf(worker));
            for (int i = pointA; i < pointB; i++) {
                worker.set(i, chB.get(i));
            }
        }

        for (int i = 0; i < a.shifts.length; i++) {
            for (int k = 0; k < a.shifts[i].length; k++) {
                for (int j = pointA; j < pointB; j++) {
                    childA.shifts[i][k][j] = b.shifts[i][k][j];
                    //childB.shifts[i][k][j] = a.shifts[i][k][j];
                }
            }
        }
        ArrayList<Chromosome> arrayList = new ArrayList<>();
        arrayList.add(childA);
        //arrayList.add(childB);
        return arrayList;
    }

    void init() {
        shifts = createShifts();
        matrix = read();
        randomMatrix();
    }

    void copy(Chromosome copy) {
        matrix.removeAll(matrix);
        shifts = new int[3][2][14];
        this.score = copy.score;
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 2; k++) {
                for (int j = 0; j < 14; j++) {
                    this.shifts[i][k][j] = copy.shifts[i][k][j];
                }
            }
        }
        for (ArrayList<Character> worker : copy.matrix) {
            ArrayList t = new ArrayList();
            for (Object aWorker : worker) t.add(aWorker);
            this.matrix.add(t);
        }
        //this.matrix.addAll(worker);
    }

    int calculateScore() {
        int score = 0;
        //считать по массиву shifts сколько не проставили
        score += UNASSIGNED_SCORE * countUnassShifts();
        //проверка на смену в выходной день
        score += X_SCORE * countShiftsInWeekend();
        //проверка на соответсвие предпочтению по отдыху и сменам
        score += D_SCORE * countDayPrefer();
        score += N_SCORE * countNightPrefer();
        score += R_SCORE * countRestPrefer();
        for (ArrayList<Character> worker : matrix) {
            //проверка на 3 ночных смены подряд
            score += MORE_NNN_SCORE * countNNN(worker);
            //проверка на день после ночи
            score += N_D_SCORE * countDayAfterN(worker);
            //проверка на отдых более трех дней
            score += LONG_REST * countLongRest(worker);
            //проверка на количество ночных смен>4
            if (!checkNumOfNights(worker)) {
                score += MORE_N_SCORE * countNumOfNights(worker);
            }
        }

        return score;
    }

    private int countUnassShifts() {
        int count = 0;
        for (int[][] way : shifts) {
            for (int[] shift : way) {
                for (int day : shift) {
                    if (day > 0)
                        count++;
                }
            }
        }
        return count;
    }

    private int countLongRest(ArrayList<Character> row) {
        int count = 0, countDay = 0;
        int r = matrix.indexOf(row);
        String a, b;
        ArrayList<Character> org = origin.get(r);
        for (int i = 1; i < row.size(); i++) {
            a = String.valueOf(org.get(i));
            b = String.valueOf(row.get(i));
            if (a.equals(b)) {
                countDay++;
            } else {
                if (countDay >= 3) count++;
                countDay = 0;
            }
        }
        if (countDay >= 3) count++;
        return count;
    }

    private int countNNN(ArrayList row) {
        int count = 0, countNight = 0;

        for (int i = 1; i < row.size(); i++) {
            if (row.get(i).toString().charAt(1) == 'n') {
                countNight++;
                if (countNight > 3) count = countNight;
            } else countNight = 0;

        }
        return count > 0 ? count - 3 : count;
    }

    private int countShiftsInWeekend() {
        int count = 0;
        String a, b;
        for (ArrayList<Character> worker : origin) {
            for (int j = 1; j < worker.size(); j++) {
                ArrayList<Character> ma = matrix.get(origin.indexOf(worker));
                a = String.valueOf(ma.get(j));
                b = String.valueOf(worker.get(j));
                if (Objects.equals(String.valueOf(worker.get(j)), "XX") && !a.equals(b)) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countDayPrefer() {
        int count = 0;
        String a;
        for (ArrayList<Character> worker : origin) {
            for (int j = 1; j < worker.size(); j++) {
                ArrayList<Character> ma = matrix.get(origin.indexOf(worker));
                a = String.valueOf(ma.get(j));
                if (Objects.equals(String.valueOf(worker.get(j)), "DD") && a.charAt(1) == 'd')
                    count++;
            }
        }
        return count;
    }

    private int countNightPrefer() {
        int count = 0;
        String a;
        for (ArrayList<Character> worker : origin) {
            for (int j = 1; j < worker.size(); j++) {
                ArrayList<Character> ma = matrix.get(origin.indexOf(worker));
                a = String.valueOf(ma.get(j));
                if (Objects.equals(String.valueOf(worker.get(j)), "NN") && a.charAt(1) == 'n')
                    count++;
            }
        }
        return count;
    }

    private int countRestPrefer() {
        int count = 0;
        String a;
        for (ArrayList worker : origin) {
            for (int j = 1; j < worker.size(); j++) {
                ArrayList ma = matrix.get(origin.indexOf(worker));
                a = String.valueOf(ma.get(j));
                if (Objects.equals(String.valueOf(worker.get(j)), "RR") && a.equals("RR"))
                    count++;
            }
        }
        return count;
    }

    private int countDayAfterN(ArrayList<Character> row) {
        int count = 0;
        String a, b;
        for (int i = 2; i < row.size(); i++) {
            a = String.valueOf(row.get(i));
            b = String.valueOf(row.get(i - 1));
            if (b.charAt(1) == 'n' && a.charAt(1) == 'd')
                count++;
        }
        return count;
    }

    private int countNumOfNights(ArrayList<Character> row) {
        int countNight = 0;
        for (Object aRow : row) {
            if (aRow == "1n" || aRow == "2n" || aRow == "3n")
                countNight++;
        }
        return countNight - 4;
    }

    int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    void mutate() {
        Chromosome cr = new Chromosome();
        cr.init();
        int n = RandomUtils.nextInt(1, 15);
        for (ArrayList<Character> worker : cr.matrix) {
            ArrayList<Character> chB = this.matrix.get(cr.matrix.indexOf(worker));
            chB.set(n, worker.get(n));
        }
        for (int i = 0; i < cr.shifts.length; i++) {
            for (int k = 0; k < cr.shifts[i].length; k++) {
                this.shifts[i][k][n - 1] = cr.shifts[i][k][n - 1];
            }
        }
    }

    private boolean checkProc() {
        double proc = RandomUtils.nextDouble();
        return proc > 0.7;
    }

    private void randomMatrix() {
        fillOne();
        fillTwo();
    }

    private int[][][] createShifts() {
        int[][][] shifts = new int[3][2][14];
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 2; k++)
                for (int j = 0; j < 14; j++) {
                    shifts[i][k][j] = 1;
                }
        }
        return shifts;
    }

    private void fillOne() {
        int count, r, num;
        String[] values = new String[]{"n", "d"};
        String y;
        //Collections.copy(matrix,origin);
        for (ArrayList worker : matrix) {
            ArrayList t = (ArrayList) worker.get(0);
            if (t.size() == 1) {
                count = 0;
                num = RandomUtils.nextInt(9);
                while (count < num) {
                    r = RandomUtils.nextInt(1, 15);
                    if (!worker.get(r).equals("1n") && !worker.get(r).equals("1d") && !worker.get(r).equals("2n") && !worker.get(r).equals("2d") && !worker.get(r).equals("3n") && !worker.get(r).equals("3d")) {
                        y = values[RandomUtils.nextInt(2)];
                        if (Objects.equals(y, "n") && checkNumOfNights(worker) && shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][1][r - 1] > 0) {
                            worker.set(r, t.get(0) + y);
                            shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][1][r - 1] = 0;
                            count++;
                        } else if (Objects.equals(y, "d") && shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][0][r - 1] > 0) {
                            worker.set(r, t.get(0) + y);
                            shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][0][r - 1] = 0;
                            count++;
                        }
                    }
                }
            }
        }
    }

    private boolean checkNumOfNights(ArrayList<Character> row) {
        int countNight = 0;
        for (Object aRow : row) {
            if (aRow == "1n" || aRow == "2n" || aRow == "3n")
                countNight++;
        }
        return countNight < 4;
    }

    private void fillTwo() {
        int count, r, num;
        String[] values = new String[]{"n", "d"};
        String y, a;
        //Collections.copy(matrix,origin);
        for (ArrayList worker : matrix) {
            ArrayList t = (ArrayList) worker.get(0);
            if (t.size() > 1) {
                count = 0;
                num = RandomUtils.nextInt(9);
                while (count < num) {
                    r = RandomUtils.nextInt(1, 15);
                    a = String.valueOf(worker.get(r));
                    if (!a.equals("1n") && !a.equals("1d") && !worker.get(r).equals("2n") && !worker.get(r).equals("2d") && !worker.get(r).equals("3n") && !worker.get(r).equals("3d")) {
                        y = values[RandomUtils.nextInt(2)];
                        if (Objects.equals(y, "n") /*&& checkNumOfNights(worker)*/) {
                            if (shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][1][r - 1] > 0) {
                                worker.set(r, t.get(0) + y);
                                shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][1][r - 1] = 0;
                                count++;
                            } else if (shifts[Integer.parseInt(String.valueOf(t.get(1))) - 1][1][r - 1] > 0) {
                                worker.set(r, t.get(1) + y);
                                shifts[Integer.parseInt(String.valueOf(t.get(1))) - 1][1][r - 1] = 0;
                                count++;
                            }
                        } else if (Objects.equals(y, "d")) {
                            if (shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][0][r - 1] > 0) {
                                worker.set(r, t.get(0) + y);
                                shifts[Integer.parseInt(String.valueOf(t.get(0))) - 1][0][r - 1] = 0;
                                count++;
                            } else if (shifts[Integer.parseInt(String.valueOf(t.get(1))) - 1][0][r - 1] > 0) {
                                worker.set(r, t.get(1) + y);
                                shifts[Integer.parseInt(String.valueOf(t.get(1))) - 1][0][r - 1] = 0;
                                count++;
                            }
                        }
                    }
                }
            }
        }
    }

    public double getProc() {
        return proc;
    }

    public void setProc(double proc) {
        this.proc = proc;
    }

    public boolean chanceToMutate() {
        double chance = 0.2;
        double val = RandomUtils.nextDouble();
        return val < chance;
    }
}
