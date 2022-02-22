package io.github.eufranio.config;

import io.leangen.geantyref.TypeToken;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;

/**
 * Created by Frani on 03/12/2018.
 */
public class Config<T> {

    private HoconConfigurationLoader loader;
    private CommentedConfigurationNode node;
    private Class<T> clazz;
    private TypeToken<T> token;
    private T value;

    public Config(Class<T> clazz, String name, File configDir) {
        if (!configDir.exists()) configDir.mkdirs();

        File file = new File(configDir, name);
        try {
            if (!file.exists()) file.createNewFile();
        } catch (Exception e) { e.printStackTrace(); }

        this.clazz = clazz;
        this.token = TypeToken.get(clazz);
        this.loader = HoconConfigurationLoader.builder()
                .file(file)
                .build();
        this.value = load(false);
    }

    private T load(boolean set) {
        try {
            this.node = this.loader.load(ConfigurationOptions.defaults().shouldCopyDefaults(true));
            T value = set ? this.value : this.node.node("config").get(token, clazz.newInstance());
            this.node.node("config").set(token, value);
            this.loader.save(this.node);
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void reload() {
        this.value = this.load(false);
    }

    public T get() {
        return this.value;
    }

    public void save() {
        this.load(true);
    }

}