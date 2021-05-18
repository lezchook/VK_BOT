import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.objects.photos.responses.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;

public class Bot {
    public static void main(String[] args) throws ClientException, ApiException, InterruptedException, IOException {
        Map<File, String> images = new HashMap<>();
        List<File> files = new ArrayList<>();
        File file = new File("C:\\Users\\leshchenko\\IdeaProjects\\untitled1\\src\\main\\resources\\tiger.jpg");
        File file2 = new File("C:\\Users\\leshchenko\\IdeaProjects\\untitled1\\src\\main\\resources\\lion.jpg");
        images.put(file, "Тигр");
        images.put(file2, "Лев");
        files.add(file);
        files.add(file2);
        int groupId = 204559474;
        String accessToken = "****";
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        int[] num = new int[1];
        num[0] = (int) (Math.random() * 2);
        Random random = new Random();
        GroupActor actor = new GroupActor(groupId, accessToken);
        UserActor actor2 = new UserActor(groupId, accessToken);
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true) {
            MessagesGetLongPollHistoryQuery historyQuery =  vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if (!messages.isEmpty()) {
                messages.forEach(message -> {
                    System.out.println(message.toString());
                    try {
                        MessageUploadResponse uploadResponse = vk.upload().photoMessage(String.valueOf(vk.photos().getMessagesUploadServer(actor).execute().getUploadUrl()), files.get(num[0])).execute();
                        List<SaveMessagesPhotoResponse> photoList = vk.photos().saveMessagesPhoto(actor, uploadResponse.getPhoto())
                                .server(uploadResponse.getServer())
                                .hash(uploadResponse.getHash())
                                .execute();
                        SaveMessagesPhotoResponse photo = photoList.get(0);
                        String attachId = "photo" + photo.getOwnerId() + "_" + photo.getId();
                        vk.messages().send(actor).attachment(attachId).userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        vk.messages().send(actor).message("Что это за животное?").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
                    catch (ApiException | ClientException e) {e.printStackTrace();}
                });
            }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(300);
        }
    }
}