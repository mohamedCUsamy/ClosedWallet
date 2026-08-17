package com.closedwallet.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.closedwallet.Entity.Merchant;
import com.closedwallet.Repository.MerchantRepository;
import com.closedwallet.enums.MerchantCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Fills the merchant table on startup. Enabled with app.seed.merchants.enabled=true
 * (see application-mohamed.properties). Skips seeding if merchants already exist.
 */
@Component
@ConditionalOnProperty(name = "app.seed.merchants.enabled", havingValue = "true")
public class MerchantSeeder implements CommandLineRunner {

    private static final String[] RESTAURANT = {
        "Cairo Grill", "Nile Bites", "Zooba Corner", "Koshary House", "Shawarma Station",
        "Olive Branch", "Pasta Republic", "Burger Yard", "Sushi Loft", "Falafel Point",
        "Tandoori Nights", "The Steak Room", "Pizza Cellar", "Mango Cafe", "Roastery No.7"
    };

    private static final String[] GROCERY = {
        "Fresh Basket", "Green Market", "Daily Mart", "Corner Grocer", "Family Foods",
        "Harvest Store", "Sunrise Supermarket", "El Nada Market", "City Grocers", "Farm Direct",
        "Pantry Plus", "Organic Aisle"
    };

    private static final String[] CLOTHING = {
        "Urban Thread", "Denim Lab", "Cotton Club", "Style Depot", "Nile Fashion",
        "The Wardrobe", "Linen & Co", "Street Fit", "Classic Tailors", "Kids Corner",
        "Sneaker Point"
    };

    private static final String[] SERVICES = {
        "QuickFix Repairs", "Bright Laundry", "City Movers", "Prime Barber", "Clean Sweep",
        "AutoCare Center", "Print Hub", "Home Wizard", "Trust Pharmacy Services", "Speedy Courier"
    };

    private static final String[] ENTERTAINMENT = {
        "Cine Star", "Play Arena", "Bowling Zone", "Escape Room 42", "Kids Land",
        "Vibe Lounge", "Game Point", "Open Air Theatre", "Karaoke Box", "Adventure Park"
    };

    private static final String[] ELECTRONICS = {
        "Tech Point", "Gadget Hub", "Mobile World", "Circuit City", "Smart Home Store",
        "Laptop Corner", "Audio Lab", "Camera House", "Pixel Store", "Power Tools & Electronics"
    };

    private static final String[] BRANCHES = {
        "Maadi", "Zamalek", "Nasr City", "Heliopolis", "Dokki", "Giza", "New Cairo",
        "Sheikh Zayed", "Mohandessin", "Alexandria", "Tanta", "Mansoura", "Aswan", "Luxor",
        "Hurghada", "Port Said", "Suez", "Ismailia", "Fayoum", "Minya"
    };

    private final MerchantRepository merchantRepository;

    @Value("${app.seed.merchants.count:200}")
    private int count;

    public MerchantSeeder(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void run(String... args) {
        if (merchantRepository.count() > 0) {
            System.out.println("[MerchantSeeder] merchants already present, skipping seed.");
            return;
        }

        Random random = new Random(42); // fixed seed => same data every run
        List<Merchant> batch = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            MerchantCategory category = pickCategory(i);
            String[] pool = poolFor(category);
            String baseName = pool[i % pool.length];
            String branch = BRANCHES[random.nextInt(BRANCHES.length)];
            String name = baseName + " - " + branch;

            Merchant merchant = new Merchant();
            merchant.setName(name);
            merchant.setEmail(slug(baseName) + "." + slug(branch) + (i + 1) + "@closedwallet.test");
            merchant.setPhone(String.format("+2010%08d", 10000000 + i));
            merchant.setCategory(category);
            merchant.setLogoPath("/logos/" + category.name().toLowerCase(Locale.ROOT) + "/" + slug(baseName) + ".png");

            batch.add(merchant);
        }

        merchantRepository.saveAll(batch);
        System.out.println("[MerchantSeeder] inserted " + batch.size() + " merchants.");
    }

    private MerchantCategory pickCategory(int index) {
        MerchantCategory[] all = MerchantCategory.values();
        return all[index % all.length];
    }

    private String[] poolFor(MerchantCategory category) {
        switch (category) {
            case RESTAURANT:    return RESTAURANT;
            case GROCERY:       return GROCERY;
            case CLOTHING:      return CLOTHING;
            case SERVICES:      return SERVICES;
            case ENTERTAINMENT: return ENTERTAINMENT;
            case ELECTRONICS:   return ELECTRONICS;
            default:            return RESTAURANT;
        }
    }

    private String slug(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
    }
}
