/*
 * MIT License
 *
 * Copyright (c) Copyright (c) 2017-2017, Greatmancode
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.greatmancode.legendarybot.commands.gif;

import com.greatmancode.legendarybot.api.commands.PublicCommand;
import com.greatmancode.legendarybot.api.plugin.LegendaryBotPlugin;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.pf4j.PluginWrapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * !gif command - Retrieve a gif from a search.
 */
public class GifCommand extends LegendaryBotPlugin implements PublicCommand {

    /**
     * The HTTP Client to do web requests
     */
    private OkHttpClient client = new OkHttpClient();

    /**
     * The config file containing the api key.
     */
    private Properties props;

    public GifCommand(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        StringBuilder builder = new StringBuilder();
        for(String s : args) {
            if (builder.length() == 0) {
                builder.append(s);
            } else {
                builder.append(" ").append(s);
            }
        }

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("rightgif.com")
                .addPathSegments("search/web")
                .build();
        FormBody body = new FormBody.Builder()
                .add("text", builder.toString())
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try {
            String result = client.newCall(request).execute().body().string();
            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result);
            event.getChannel().sendMessage(object.get("url").toString()).queue();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 99;
    }

    @Override
    public String help(Guild guild) {
        return getBot().getTranslateManager().translate(guild,"command.gif.longhelp");
    }

    @Override
    public String shortDescription(Guild guild) {
        return getBot().getTranslateManager().translate(guild,"command.gif.shorthelp");
    }

    @Override
    public void start() {
        //Load the configuration
        props = new Properties();
        try {
            props.load(new FileInputStream("app.properties"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
            getBot().getStacktraceHandler().sendStacktrace(e);
        }
        getBot().getCommandHandler().addCommand("gif", this, "Fun Commands");
    }

    @Override
    public void stop() {
        getBot().getCommandHandler().removeCommand("gif");
    }
}
