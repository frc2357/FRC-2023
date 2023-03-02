package pitscript;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;


    public class MakeFileList {
        MakeFileList instance = null;
        public MakeFileList getInstance() {
            instance = this;
            return instance;}
        public List<String> getFileList(String SFTPWORKINGDIR, ChannelSftp channelSftp) {
                List<String> fileList = new ArrayList<String>();
                try {
                    channelSftp.cd(SFTPWORKINGDIR);
                } catch (SftpException e) {
                    System.out.println("the channelSftp variable did not work. -------");
                    e.printStackTrace();
                }
            try{
                
                
                Vector fileVector = channelSftp.ls(SFTPWORKINGDIR);
                for(int i=0; i<fileVector.size();i++){
                    System.out.println(fileVector.get(i).toString());
                    fileList.add(fileVector.get(i).toString());
                }
                return fileList;
            }catch(Exception ex){
                System.out.println("it didnt get the stuff for the file or someting, the file lister broke :( ------------");
                ex.printStackTrace();
            }
            return null;
        }

}