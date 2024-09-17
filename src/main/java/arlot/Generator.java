package arlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Generator {
    private static final String[] FIRST_NAMES = {
            "John", "Jane", "Alex", "Emily", "Michael", "Sarah", "David", "Laura", "Chris", "Emma",
            "Daniel", "Olivia", "Matthew", "Sophia", "James", "Isabella", "Andrew", "Mia", "Joshua", "Charlotte",
            "Jacob", "Ethan", "Abigail", "Benjamin", "Grace", "Mason", "Ava", "Logan", "Amelia", "Elijah",
            "Chloe", "Lucas", "Madison", "Jackson", "Ella", "Aiden", "Scarlett", "Henry", "Victoria", "Samuel"
    };

    private static final String[] MIDDLE_NAMES = {
            "Allen", "Marie", "Lee", "Grace", "James", "Louise", "Ray", "Rose", "Drew", "Ann",
            "Blake", "Elizabeth", "Jay", "Lynn", "Paul", "Jean", "Taylor", "Claire", "Scott", "Faith",
            "John", "Mae", "Ryan", "Hope", "George", "Eve", "Ruth", "Jade", "Frank", "Joy",
            "Thomas", "Fay", "Kyle", "Sue", "Patrick", "Lily", "Jack", "May", "Edward", "June"
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Brown", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris", "Martin",
            "Thompson", "Garcia", "Martinez", "Robinson", "Clark", "Rodriguez", "Lewis", "Lee", "Walker", "Hall",
            "Allen", "Young", "Hernandez", "King", "Wright", "Lopez", "Hill", "Scott", "Green", "Adams",
            "Baker", "Gonzalez", "Nelson", "Carter", "Mitchell", "Perez", "Roberts", "Turner", "Phillips", "Campbell"
    };

    public static String randomName(int maxLen, int level) {
        Random random = new Random();

        int nameType = random.nextInt(1, maxLen+1);
        StringBuilder newName = new StringBuilder();
        int bound = FIRST_NAMES.length-1;
        if (level >= 2) {
            bound += MIDDLE_NAMES.length-1;
        }
        if (level >= 3) {
            bound += LAST_NAMES.length;
        }
        int nameAt;
        for (int i=0; i<nameType; i++) {
            nameAt = random.nextInt(bound);
            if (nameAt>=FIRST_NAMES.length) {
                if (nameAt>=(FIRST_NAMES.length+MIDDLE_NAMES.length)) {
                    nameAt /= 3;
                    newName.append(LAST_NAMES[nameAt]);
                } else {
                    nameAt /= 2;
                    newName.append(MIDDLE_NAMES[nameAt]);
                }
            } else {
                newName.append(FIRST_NAMES[nameAt]);
            }
            newName.append(" ");
        }
        newName.deleteCharAt(newName.length()-1);
        return newName.toString();
    }
    public static String randomName(int maxLen) {
        return randomName(maxLen, 3);
    }
    public static String randomName() {
        return randomName(3, 3);
    }
}
