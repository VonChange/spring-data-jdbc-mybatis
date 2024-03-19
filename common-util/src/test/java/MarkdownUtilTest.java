import com.vonchange.common.util.MarkdownUtil;
import org.junit.Test;

public class MarkdownUtilTest {


    @Test
    public void markdownUtilTest(){
        String content =  MarkdownUtil.getContent("config.test.sql2");
        System.out.println(content);

    }
    @Test
    public void markdownUtilTest2(){
        String content =  MarkdownUtil.getContent("config.test.sql1");
        System.out.println(content);
    }


}
