import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {
    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        List<String> all = new ArrayList<String>();
        String base = "http://predb.me/?cats=tv&page=";
        for (int i = 200; i > 1; --i)
            all.addAll(getPage(base + String.valueOf(i) + "", true));
        all.addAll(getPage("http://predb.me/?cats=tv", true));
        long end = System.nanoTime();
        long elap = end - start;
        System.out.println("Gathered all 200 pages in ~ " + elap + " nano");
        System.out.println("Gathered: " + all.size() + "( ~8k ?)");
    }

    public static List<String> getPage(String targetURL, boolean prntDbg) throws IOException {
        List<String> result = new ArrayList<String>();
        Document doc = null;
        try {
            doc = Jsoup.connect(targetURL).userAgent("Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0").ignoreHttpErrors(true).timeout(5000).get();
        } catch (Exception e) {
            System.err.println("msg = " + e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("size of elements = " + doc.getAllElements().size());
        Elements entries = doc.select("div");
        //System.out.println("entries size = " + entries.size());
        for (Element ele : entries) System.out.println(ele.html());
        int count = 0;
        for (Element div : entries) {
            if (!div.hasClass("post") || !div.hasAttr("id"))
                continue;
            count++;
            Elements children = div.children();

            String datetime = children.select("span").first().attr("title");
            String parse = datetime.substring(13, datetime.length());
            parse = parse.replaceAll(" ", "");

            String base = children.get(0).text();
            String[] attrs = base.split(" ");
            attrs[0] = attrs[0] + "_" + parse;

            String entry = String.format("%s_%s_%s", attrs[0], attrs[1], attrs[2]);
            result.add(entry);
            if (prntDbg)
                System.out.println(entry);
        }
        //if (prntDbg)
            //System.out.println(count);
        return result;
    }
}
