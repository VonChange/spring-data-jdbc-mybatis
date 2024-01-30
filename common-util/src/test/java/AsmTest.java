import com.esotericsoftware.reflectasm.MethodAccess;
import org.junit.Test;

import java.time.LocalDateTime;

public class AsmTest {
    @Test
    public void asmParentTest(){
        UserModel userModel = new UserModel();
        MethodAccess ma = MethodAccess.get(UserModel.class);
        int index = ma.getIndex("setUpdateTime");
        for (String methodName : ma.getMethodNames()) {
            System.out.println(methodName);
        }
        System.out.println(ma.getClass());

        ma.invoke(userModel, index, LocalDateTime.now());
        System.out.println(userModel.getUpdateTime());
    }

}
