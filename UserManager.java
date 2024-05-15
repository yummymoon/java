import java.util.ArrayList;
import java.util.Scanner;

class User {
    private String name;
    private int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + '}';
    }
}

public class UserManager {
    private ArrayList<User> users;
    private Scanner scanner;

    public UserManager() {
        users = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Delete User");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    viewUsers();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine(); // consume newline

        User user = new User(name, age);
        users.add(user);
        System.out.println("User added successfully.");
    }

    private void viewUsers() {
        if (users.isEmpty()) {
            System.out.println("No users to display.");
        } else {
            for (User user : users) {
                System.out.println(user);
            }
        }
    }

    private void deleteUser() {
        System.out.print("Enter name of user to delete: ");
        String name = scanner.nextLine();

        boolean found = false;
        for (User user : users) {
            if (user.getName().equals(name)) {
                users.remove(user);
                System.out.println("User deleted successfully.");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("User not found.");
        }
    }

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        userManager.run();
    }
}
