package com.anki.anki_api.config;

import com.anki.anki_api.entity.*;
import com.anki.anki_api.repository.AnkiCardRepository;
import com.anki.anki_api.repository.LearningHistoryRepository;
import com.anki.anki_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnkiCardRepository ankiCardRepository;

    @Autowired
    private LearningHistoryRepository learningHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Only seed if no users exist
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("Seeding database...");

        String defaultPassword = passwordEncoder.encode("123456");
        Random random = new Random();

        // 1. Create Teachers
        User teacher1 = User.builder()
                .username("teacher1")
                .password(defaultPassword)
                .email("teacher1@anki.com")
                .fullName("Mr. Teacher One")
                .role(Role.ROLE_TEACHER)
                .build();
        
        User teacher2 = User.builder()
                .username("teacher2")
                .password(defaultPassword)
                .email("teacher2@anki.com")
                .fullName("Ms. Teacher Two")
                .role(Role.ROLE_TEACHER)
                .build();

        userRepository.saveAll(List.of(teacher1, teacher2));

        // 2. Create Students
        List<User> students = new ArrayList<>();
        // 5 for teacher 1
        for (int i = 1; i <= 5; i++) {
            students.add(User.builder()
                    .username("student" + i + "_t1")
                    .password(defaultPassword)
                    .email("s" + i + "t1@anki.com")
                    .fullName("Student " + i + " of T1")
                    .role(Role.ROLE_STUDENT)
                    .build());
        }
        // 5 for teacher 2
        for (int i = 1; i <= 5; i++) {
            students.add(User.builder()
                    .username("student" + i + "_t2")
                    .password(defaultPassword)
                    .email("s" + i + "t2@anki.com")
                    .fullName("Student " + i + " of T2")
                    .role(Role.ROLE_STUDENT)
                    .build());
        }
        students = userRepository.saveAll(students);

        // 3. Create Cards
        List<AnkiCard> cards = new ArrayList<>();
        String[][] wordData = {
            {"Epiphany", "/əˈpifənē/", "A moment of sudden revelation or insight", "HARD"},
            {"Serendipity", "/ˌserənˈdipədē/", "The occurrence of events by chance in a happy way", "MEDIUM"},
            {"Petrichor", "/ˈpeˌtrīkôr/", "A pleasant smell that frequently accompanies the first rain", "EASY"},
            {"Luminescence", "/ˌlo͞oməˈnesəns/", "The emission of light by a substance not resulting from heat", "HARD"},
            {"Solitude", "/ˈsäləˌt(y)o͞od/", "The state or situation of being alone", "EASY"},
            {"Aurora", "/əˈrôrə/", "A natural electrical phenomenon characterized by reddish or greenish lights", "MEDIUM"},
            {"Euphoria", "/yo͞oˈfôrēə/", "A feeling or state of intense excitement and happiness", "MEDIUM"},
            {"Vignette", "/vinˈyet/", "A brief evocative description, account, or episode", "HARD"},
            {"Ineffable", "/inˈefəb(ə)l/", "Too great or extreme to be expressed or described in words", "HARD"},
            {"Mellifluous", "/məˈliflo͞oəs/", "A sound that is sweet and musical; pleasant to hear", "HARD"},
            {"Clinomania", "/ˌklī.nəˈmeɪ.ni.ə/", "Excessive desire to stay in bed", "MEDIUM"},
            {"Sehnsucht", "/ˈzeenˌzoocht/", "A deep longing for something indefiniable", "HARD"},
            {"Hiraeth", "/ˈhɪraɪθ/", "A homesickness for a home to which you cannot return", "HARD"},
            {"Nefarious", "/niˈfe(ə)rēəs/", "Wicked or criminal", "MEDIUM"},
            {"Sonorous", "/ˈsänərəs/", "An impossibly deep and full sound", "EASY"},
            {"Limerence", "/ˈlimər(ə)ns/", "The state of being infatuated with another person", "MEDIUM"},
            {"Bombinate", "/ˈbäm.bə.neɪt/", "To make a humming or buzzing noise", "EASY"},
            {"Ethereal", "/əˈTHirēəl/", "Extremely delicate and light in a way that seems too perfect for this world", "MEDIUM"},
            {"Illicit", "/i(l)ˈlisit/", "Forbidden by law, rules, or custom", "EASY"},
            {"Halcyon", "/ˈhalsēən/", "Denoting a period of time in the past that was idyllically happy and peaceful", "HARD"}
        };

        for (String[] data : wordData) {
            cards.add(AnkiCard.builder()
                    .word(data[0])
                    .pronunciation(data[1])
                    .definition(data[2])
                    .difficulty(Difficulty.valueOf(data[3]))
                    .build());
        }
        cards = ankiCardRepository.saveAll(cards);

        // 4. Create Random History
        List<LearningHistory> history = new ArrayList<>();
        // Assign cards to students randomly
        for (User student : students) {
            // Each student reviews 5 random cards
            for (int i = 0; i < 5; i++) {
                AnkiCard randomCard = cards.get(random.nextInt(cards.size()));
                history.add(LearningHistory.builder()
                        .student(student)
                        .card(randomCard)
                        .reviewDate(LocalDateTime.now().minusDays(random.nextInt(30))) // last 30 days
                        .rating(Difficulty.values()[random.nextInt(Difficulty.values().length)])
                        .build());
            }
        }
        learningHistoryRepository.saveAll(history);

        System.out.println("Database seeded successfully!");
    }
}
