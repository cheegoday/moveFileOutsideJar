package sdkdemo.powerboot;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// java -jar sdkdemo-0.0.1-SNAPSHOT.jar --fromPath=classpath:dll/ --toPath=D://java//dll//
@Component
@Order(value = 1)
public class FileMoveForJar implements ApplicationRunner {
    @Value("${toPath}")
    private String toPath;
    @Value("${fromPath}")
    private String fromPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!this.getClass().getResource("/").getPath().contains("jar")) {
            return;
        }
        ArrayList<String> files = new ArrayList<String>();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(fromPath + "**"); //一个*匹配所有文件，两个*匹配所有子目录和文件
        Pattern pattern = Pattern.compile(".*\\/dll\\/(.*)\\/");

        //创建toPath目录
        if (!new File(toPath).exists()){
            new File(toPath).mkdirs();
        }

        //在toPath下创建所有子目录
        for (Resource resource : resources) {
            String str = resource.getURI().toString();
            String part = str.substring(str.length() - 12); //截取后几位
            if (!part.contains(".")) {
                Matcher matcher = pattern.matcher(resource.getURI().toString());
                if (matcher.find()) {
                    String tailString = matcher.group(1);
                    String subDirectory = toPath + tailString.replaceAll("/", "//");
                    if (!new File(subDirectory).exists()){
                        new File(subDirectory).mkdirs();
                    }
                }
            }else {
                files.add(str);
            }
        }

        //将所有dll文件通过流的形式复制到jar包外部目录toPath下
        Pattern pattern2 = Pattern.compile(".*\\/dll\\/(.*)");
        Pattern pattern3 = Pattern.compile("(.*)\\/(.*)");
        for (String path: files){
            Matcher matcher2 = pattern2.matcher(path);
            if (matcher2.find()) {
                String tailString = matcher2.group(1);
                Matcher matcher3 = pattern3.matcher(tailString);
                InputStream inStream = this.getClass().getResourceAsStream("/dll/"+tailString);
                if (matcher3.find()){//dll目录下有子目录
                    FileUtils.copyInputStreamToFile(inStream, new File(toPath+matcher3.group(1), matcher3.group(2)));
                }else {//dll目录下有子目录直接是文件
                    FileUtils.copyInputStreamToFile(inStream, new File(toPath, tailString));
                }
            }
        }






    }
}
