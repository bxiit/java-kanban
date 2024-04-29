package adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration duration) throws IOException {
        if (duration == null) {
            out.nullValue();
            return;
        }
        out.value(durationToString(duration));
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        return durationFromString(in.nextString());
    }

    private String durationToString(Duration duration) {
        return "%02d:%02d:%02d".formatted(duration.toDays(), duration.toHoursPart(), duration.toMinutesPart());
    }

    private Duration durationFromString(String duration) {
        if (duration.isEmpty()) {
            return null;
        }
        String[] dayHourMinute = duration.split(":");
        int days = Integer.parseInt(dayHourMinute[0]);
        int hours = Integer.parseInt(dayHourMinute[1]);
        int minutes = Integer.parseInt(dayHourMinute[2]);

        return Duration.ofDays(days)
                .plusHours(hours)
                .plusMinutes(minutes);
    }
}
