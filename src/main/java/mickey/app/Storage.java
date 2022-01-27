package mickey.app;

import mickey.task.Deadline;
import mickey.task.Event;
import mickey.task.Task;
import mickey.task.TaskList;
import mickey.task.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public List<Task> load() throws Exception {
        List<Task> tasks = new ArrayList<>();
        File f = new File(filePath);
        if (f.exists()) {
            try {
                Scanner sc = new Scanner(f);
                while (sc.hasNextLine()) {
                    String[] toAdd = sc.nextLine().split("\\|");
                    Task t;

                    switch (toAdd[0]) {
                        case "T":
                            t = new ToDo(toAdd[2]);
                            break;
                        case "D":
                            t = new Deadline(toAdd[2], toAdd[3]);
                            break;
                        case "E":
                            t = new Event(toAdd[2], toAdd[3]);
                            break;
                        default:
                            throw new Exception();
                    }

                    if (toAdd[1].equals("1")) {
                        t.markAsDone();
                    }
                    tasks.add(t);
                }
            } catch (FileNotFoundException e) {
                System.out.println("Something went wrong: " + e.getMessage());
            }
        }
        return tasks;
    }

    public void save(TaskList tasks) {
        try {
            FileWriter fw = new FileWriter(filePath);
            for (Task t : tasks.getTasks()) {
                StringBuilder newTask = new StringBuilder();
                if (t instanceof ToDo) {
                    newTask.append("T|");
                } else if (t instanceof Deadline) {
                    newTask.append("D|");
                } else {
                    newTask.append("E|");
                }

                if (t.isDone) {
                    newTask.append("1|");
                } else {
                    newTask.append("0|");
                }

                newTask.append(t.description);

                if (t instanceof Deadline) {
                    newTask.append("|");
                    newTask.append(((Deadline) t).by.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm")));
                } else if (t instanceof Event) {
                    newTask.append("|");
                    newTask.append(((Event) t).at.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm")));
                }
                newTask.append("\r\n");
                fw.write(newTask.toString());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }
}