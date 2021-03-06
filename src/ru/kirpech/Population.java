package ru.kirpech;

import java.util.ArrayList;

public class Population {
    int numberOfMembers;
    int numberOfIterations;
    ArrayList<Chromosome> members;

    Population(int numberOfMembers, String fin) {
        this.numberOfMembers = numberOfMembers;
        Chromosome.setOrigin(fin);
    }

    public void createPopulation() {
        members = new ArrayList<>();
        for (int i = 0; i < this.numberOfMembers; i++) {
            Chromosome m = new Chromosome();
            m.init();
            members.add(m);
        }
    }

    public void setNumberOfIterations(int iterations) {
        this.numberOfIterations = iterations;
    }

    static void print(Chromosome print) {
        Chromosome.print(print);
    }

    public void startGA() {
        int bestScore = Integer.MIN_VALUE;
        Chromosome bestSolution = new Chromosome();
        bestSolution.copy(members.get(getBestMemberIndex()));
        for (int epoch = 0; epoch < this.numberOfIterations; epoch++) {
            //размножение
            System.out.println(epoch);
            System.out.println("-------------------------------------------------");
            calculateScore();
            if (bestScore < members.get(0).getScore()) {
                bestSolution.copy(members.get(0));
                bestScore = bestSolution.getScore();
            }
            crossing();
            //мутация
            mutation();
            //отбор
            calculateScore();
            selection();
            /*for (Chromosome member : members) {
                print(member);
                System.out.println(member.getScore());
                System.out.println();
            }*/

            if (epoch % 500 == 0) {
                print(bestSolution);
                System.out.println(bestScore);
            }
        }
        bestSolution.calculateScore();
        print(bestSolution);
        System.out.println(bestScore);
    }

    private int getBestMemberIndex() {
        int maxScore = Integer.MIN_VALUE;
        for (Chromosome member : members) {
            if (member.getScore() > maxScore) {
                maxScore = member.getScore();
            }
        }
        for (Chromosome member : members) {
            if (member.getScore() > maxScore) {
                return members.indexOf(member);
            }
        }
        return 0;
    }

    private void calculateScore() {
        for (Chromosome member : members) {
            member.setScore(member.calculateScore());
        }
    }

    private void crossing() {
        quickSort();
        countProc();
        choose();
    }

    private void choose() {
        double parentA, parentB;
        for (int i = 0; i < numberOfMembers / 2; i++) {
            parentA = RandomUtils.nextDouble();
            parentB = RandomUtils.nextDouble();
            Chromosome a = members.get(binarySearch(parentA));
            Chromosome b = members.get(binarySearch(parentB));
            members.addAll(Chromosome.crossing(a, b));
        }
    }

    private int binarySearch(double proc) {
        int i = -1;
        if (members.size() != 0) {
            int low = 0, high = members.size(), mid = 0;
            while (low < high) {
                mid = (low + high) / 2;
                if (proc == members.get(mid).getProc()) {
                    i = mid;
                    break;
                } else {
                    if (proc > members.get(mid).getProc()&&mid>0) {
                        if(proc<members.get(mid-1).getProc()) {i=mid; break;}
                        else high = mid;
                    } else {
                        low = mid + 1;
                    }
                }
            }
            if (low >= high) i = mid;
        }
        return i;
    }

    private void countProc() {
        double a, b;
        a = RandomUtils.nextDouble(1, 2);
        b = 2 - a;

        double proc;
        for (Chromosome member : members) {
            proc = (a - (a - b) * ((members.indexOf(member)) / (numberOfMembers - 1))) / numberOfMembers;
            member.setProc(proc);
        }

    }

    private void mutation() {
        for (Chromosome member : members) {
            if (member.chanceToMutate())
                member.mutate();
        }
    }

    private void selection() {
        doSort(0, members.size() - 1);
        while (members.size() > numberOfMembers) {
            members.remove(numberOfMembers);
        }
    }

    public void quickSort() {
        int startIndex = 0;
        int endIndex = this.numberOfMembers - 1;
        doSort(startIndex, endIndex);
    }

    private void doSort(int start, int end) {
        if (start >= end)
            return;
        int i = start, j = end;
        int cur = i - (i - j) / 2;
        while (i < j) {
            while (i < cur && (this.members.get(i).getScore() >= this.members.get(cur).getScore())) {
                i++;
            }
            while (j > cur && (this.members.get(cur).getScore() >= this.members.get(j).getScore())) {
                j--;
            }
            if (i < j) {
                Chromosome temp = this.members.get(i);
                this.members.set(i, this.members.get(j));
                this.members.set(j, temp);
                if (i == cur)
                    cur = j;
                else if (j == cur)
                    cur = i;
            }
        }
        doSort(start, cur);
        doSort(cur + 1, end);
    }

    public Chromosome getBestChromosome(){
        return members.get(0);
    }
    public void addChromosome(Chromosome cr){
        members.add(cr);
    }
}
