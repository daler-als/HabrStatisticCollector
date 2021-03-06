package com.newbilius.HabrStatisticCollector.AnalyticsGenerators;

import com.newbilius.HabrStatisticCollector.CSVWriteHelper;
import com.newbilius.HabrStatisticCollector.HabrDataLoader.HabrItem;

import java.io.IOException;
import java.util.HashMap;

public abstract class SummaryBySomethingInfo implements IAnalyticsGenerator {
    class SomethingInfo {
        String Title;
        int Bookmarks;
        int Views;
        int Comments;
        int Score;

        SomethingInfo(String title) {
            Title = title;
        }
    }

    private HashMap<String, SomethingInfo> somethingInfo = new HashMap<>();

    public abstract String getSomethingName();

    public abstract String getFileName();

    public abstract String[] getSomethingValues(HabrItem item);

    @Override
    public void generate(HabrItem[] items) {
        prepareInfo(items);
        saveInfo();
    }

    private void prepareInfo(HabrItem[] items) {
        for (var item : items) {
            var values = getSomethingValues(item);
            if (values == null)
                continue;

            for (var value : values) {
                var valueInLowerKeys = value.toLowerCase();

                if (!somethingInfo.containsKey(valueInLowerKeys))
                    somethingInfo.put(valueInLowerKeys, new SomethingInfo(value));

                var somethingInfo = this.somethingInfo.get(valueInLowerKeys);
                somethingInfo.Bookmarks += item.Bookmarks;
                somethingInfo.Views += item.Views;
                somethingInfo.Comments += item.Comments;
                somethingInfo.Score += item.Score;
            }
        }
    }

    private void saveInfo() {
        try {
            try (var csvWriter = CSVWriteHelper.createCSVWriter(getFileName() + ".csv")) {
                csvWriter.writeNext(new String[]{
                        getSomethingName(),
                        "Плюсы",
                        "Закладки",
                        "Комментарии",
                        "Просмотры"
                });

                for (var hub : somethingInfo.values()) {
                    csvWriter.writeNext(new String[]{
                            hub.Title,
                            String.valueOf(hub.Score),
                            String.valueOf(hub.Bookmarks),
                            String.valueOf(hub.Comments),
                            String.valueOf(hub.Views)
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}