package org.giftorg.common.bigmodel.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWTUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.giftorg.common.bigmodel.BigModel;
import org.giftorg.common.elasticsearch.Elasticsearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ChatGLM implements BigModel {
    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v3/model-api/text_embedding/invoke";
    private static final String API_KEY = "700b86ec78faa1ad8af5c4b01594e15d.UzSKKYtCkSPrn7CP";

    @Override
    public String chat(List<Message> messages) throws Exception {
        throw new Exception("not implemented");
    }

    public List<Double> textEmbedding(String prompt) throws IOException {
        TextEmbeddingRequest body = new TextEmbeddingRequest(prompt);

        HttpResponse resp = HttpRequest.post(API_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", genAuthorization(API_KEY, 360))
                .body(JSON.toJSONBytes(body))
                .execute();

        TextEmbeddingResponse res = JSONUtil.toBean(resp.body(), TextEmbeddingResponse.class);

        if (!res.success) {
            log.error(" response error, response.body: {}", resp.body());
            throw new RuntimeException("chat response error, response.body: " + resp.body());
        }
        return res.data.embedding;
    }

    public static class TextEmbeddingRequest {
        public String prompt;

        public TextEmbeddingRequest(String prompt) {
            this.prompt = prompt;
        }
    }

    public static String genAuthorization(String apiKey, long expSeconds) {
        String[] parts = apiKey.split("\\.");
        if (parts.length != 2) {
            throw new RuntimeException("chatglm invalid api key");
        }

        String id = parts[0];
        String secret = parts[1];
        Map<String, Object> payload = new HashMap<>();

        long currentTimeMillis = System.currentTimeMillis();
        long expirationTimeMillis = currentTimeMillis + (expSeconds * 1000L);
        payload.put("api_key", id);
        payload.put("exp", expirationTimeMillis);
        payload.put("timestamp", currentTimeMillis);

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("alg", "HS256");
        headerMap.put("sign_type", "SIGN");

        return JWTUtil.createToken(headerMap, payload, secret.getBytes(StandardCharsets.UTF_8));
    }

    @Data
    public static class TextEmbeddingResponse {
        public Integer code;
        public String msg;
        public Boolean success;
        public TextEmbeddingResponseData data;
    }

    @Data
    public static class TextEmbeddingResponseData {
        public List<Double> embedding;
    }

    public static void main(String[] args) throws IOException {
        ChatGLM glm = new ChatGLM();

//        List<Double> hellos = glm.textEmbedding("hello");
//        log.info(hellos.toString());

        String index = "es_text";

//        glm.esClient.indices().create(c -> c
//                .index(index)
//                .mappings(m -> {
//                    m.properties("text", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")));
//                    m.properties("embedding", p -> p.denseVector(d -> d.dims(1024)));
//                    return m;
//                })
//        );


//        List<Double> vector = new List<Double>{-0.26067113876342773, -0.008145123720169067, -0.2204902172088623, -0.020439527928829193, -0.03657594323158264, 0.016266867518424988, -0.15039795637130737, 0.24716486036777496, 0.05303326994180679, 0.08859050273895264, -0.07186281681060791, 0.11873932182788849, 0.020788155496120453, 0.12481792271137238, 0.027049167081713676, -0.16869525611400604, 0.001648799516260624, 0.03763104975223541, -0.06157363951206207, 0.06940200924873352, -0.07310710102319717, 0.09545294940471649, -0.07113616168498993, -0.1752775013446808, 0.20352062582969666, -0.20757366716861725, -0.0580005869269371, -0.20368561148643494, -0.01818995177745819, -0.2533513009548187, 0.27013900876045227, -0.1240704208612442, -0.004977710545063019, 0.0994919091463089, 0.0889534205198288, 0.1339770257472992, 0.12125971913337708, 0.1296570748090744, 0.21814660727977753, -0.12848542630672455, -0.03735263645648956, 0.026080362498760223, 0.013281874358654022, -0.08595113456249237, 0.05968281999230385, 0.045409005135297775, 0.012307623401284218, 0.04826531559228897, -0.13312704861164093, -0.04497599974274635, 0.05575747415423393, 0.05995788425207138, -0.031567927449941635, 0.19018639624118805, 0.021404214203357697, 0.0043648406863212585, 0.03902371972799301, 0.09812376648187637, 0.014402475208044052, 0.1440022587776184, -0.03449619561433792, 0.17931586503982544, -0.10466774553060532, 0.1253320574760437, -0.05594637244939804, -0.10326138138771057, 0.0471629835665226, 0.10224694013595581, -0.05249309539794922, 0.0622599720954895, 0.022786471992731094, -0.002042606472969055, -0.07573272287845612, 0.09714939445257187, 0.17744535207748413, -0.1340441107749939, -0.13870498538017273, 0.07893742620944977, 0.09988139569759369, 0.26701366901397705, -0.1141500473022461, 0.29597967863082886, 0.09593170136213303, -0.11077103763818741, 0.24851873517036438, -0.03872770816087723, 0.04452591389417648, 0.18665163218975067, 0.1463070809841156, 0.004172489047050476, -0.08285949379205704, 0.32203349471092224, 0.016033023595809937, -0.04247574880719185, -0.029649969190359116, 0.012130707502365112, 0.2946329712867737, -0.03112022578716278, 0.07283414155244827, -0.19158834218978882, 0.12914659082889557, -0.05226248502731323, -0.20968438684940338, -0.020910881459712982, -0.15839707851409912, 0.1225476786494255, -0.11820996552705765, -0.040525197982788086, 0.04156424105167389, -0.14928659796714783, 0.09254994988441467, -0.07171445339918137, -0.19712038338184357, 0.2529025077819824, -0.0693848580121994, -0.13627223670482635, -0.08647695928812027, 0.14014115929603577, 0.2100977748632431, -0.10071173310279846, -0.1458645612001419, 0.1925140917301178, 0.05080622062087059, 0.005331315100193024, 0.05556512624025345, -0.0081088338047266, -0.038757629692554474, -0.05767863988876343, 0.004637222737073898, -0.22459568083286285, 0.14641785621643066, -0.024842537939548492, -0.04817480221390724, 0.19742390513420105, -0.13262279331684113, 0.01098804920911789, -0.035064879804849625, 0.06595875322818756, -0.04652756080031395, -0.1357252597808838, 0.1821681559085846, -0.026421410962939262, 0.1344469040632248, -0.01067100465297699, -0.35015642642974854, 0.026901911944150925, -0.010108210146427155, -0.09276929497718811, 0.07122005522251129, 0.12100797891616821, 0.17001540958881378, 0.35139405727386475, -0.013911448419094086, 0.056537896394729614, -0.10954569280147552, -0.024106770753860474, -0.17878741025924683, -0.027429692447185516, -0.21370553970336914, 0.1166691780090332, -0.06705081462860107, -0.07461348176002502, -0.066407211124897, 0.009708285331726074, -0.4612538814544678, 0.05380646511912346, -0.2054826319217682, -0.013849183917045593, 0.1500304937362671, -0.2347584217786789, 0.011113171465694904, -0.09295304119586945, 0.10237133502960205, -0.05534076690673828, 0.10059528052806854, -0.1864108145236969, 0.1164877787232399, -0.16558963060379028, 0.011760422959923744, -0.013066504150629044, 0.05880087614059448, -0.01729615032672882, 0.09289205074310303, 0.024046383798122406, -0.0796177089214325, -0.09356383979320526, -0.13680559396743774, -0.18734927475452423, -0.007577784359455109, 0.26935720443725586, -0.11845133453607559, 0.03051963821053505, -0.2033739686012268, 0.0680638998746872, 0.1301664561033249, 0.15774333477020264, 0.24956518411636353, 0.004504406824707985, 0.08251112699508667, 0.22355125844478607, -0.04253661632537842, -0.21515600383281708, -0.0495283380150795, 0.003016519360244274, 0.2965991199016571, 0.017770014703273773, -0.007032240275293589, 0.0235133096575737, 0.10209839791059494, -0.03825926408171654, -0.31111428141593933, 0.10146494209766388, 0.046224549412727356, 0.02694200724363327, 0.0985097736120224, 0.24018189311027527, 0.13256853818893433, 0.018418125808238983, 0.14054037630558014, 0.12836799025535583, -0.06015150621533394, -0.11704671382904053, 0.19968737661838531, -0.042339351028203964, 0.11542490124702454, -0.6221805810928345, -0.007955430075526237, 0.17487181723117828, 0.11068878322839737, 0.12139317393302917, -0.12258974462747574, 0.0560249499976635, 0.06622347980737686, 0.20345765352249146, 0.15439322590827942, -0.18764753639698029, -0.12840069830417633, 0.16947147250175476, 0.1267334222793579, 0.20019903779029846, -0.026566924527287483, 0.050200000405311584, -0.45369234681129456, -0.019575845450162888, 0.6129833459854126, -0.045195501297712326, -0.058790646493434906, 0.16377471387386322, -0.06379130482673645, 0.20854401588439941, -0.1051458790898323, -0.056379787623882294, -0.19063080847263336, -0.30019932985305786, -0.06950706988573074, 0.1920657753944397, 0.05214680731296539, 0.09525126218795776, 0.08355273306369781, 0.16137591004371643, -0.048287682235240936, 0.055989496409893036, 0.020440608263015747, -0.14205452799797058, 0.18889394402503967, 0.07243803888559341, 0.3120173513889313, 0.21299119293689728, -0.08189256489276886, -0.06435590237379074, -0.046288542449474335, -0.14738810062408447, 0.05856843292713165, -0.1745060533285141, 0.16546064615249634, 0.12293972074985504, 0.01198580488562584, -0.130753755569458, -0.18429303169250488, 0.11272630095481873, 0.17316892743110657, 0.1887674629688263, -0.07643073052167892, 0.13283470273017883, -0.6630281209945679, 0.049628742039203644, 0.0011481853434816003, -0.006534719839692116, -0.07858256250619888, 0.15852484107017517, -0.1225610300898552, -0.15617811679840088, -0.03396657854318619, 0.2255101203918457, -0.11575216799974442, -0.1574009507894516, -0.18986934423446655, -0.19153746962547302, 0.08885812759399414, 0.023888690397143364, 0.13091574609279633, -0.09973625838756561, 0.011457208544015884, -0.190449059009552, -0.1782548725605011, 0.1372089833021164, -0.04839079827070236, 0.10628218203783035, 0.01785137504339218, 0.2921208143234253, -0.012809429317712784, -0.05670102685689926, 0.0018587913364171982, 0.10390999168157578, -0.04589136317372322, -0.20522283017635345, -0.07046738266944885, 0.3431206941604614, 0.059602461755275726, -0.15941080451011658, -0.26266005635261536, 0.17070631682872772, 0.03750022128224373, -0.15044550597667694, 0.17846481502056122, -0.052223943173885345, -0.08887355774641037, -0.10144288092851639, -0.1406124085187912, 0.1834409385919571, 0.03312335163354874, 0.029242895543575287, 0.1497979760169983, -0.022802477702498436, -0.438311368227005, -0.21905389428138733, -0.20064349472522736, -0.1156342625617981, -0.2156946063041687, -0.3519721031188965, 0.1401854157447815, -0.1290854960680008, -0.030019834637641907, 0.07291872799396515, -0.020489100366830826, 0.33208054304122925, 0.20477920770645142, -0.4816162586212158, 0.160137340426445, 0.2901786267757416, 0.09049243479967117, -0.1089961975812912, 0.178335040807724, -0.08786500245332718, 0.12939758598804474, -0.04481257498264313, 0.09658387303352356, -0.130550816655159, 0.21715345978736877, 0.22359907627105713, 0.34798571467399597, 0.02972598373889923, 0.14192937314510345, 0.05043337494134903, 0.004014220088720322, -0.09819604456424713, -0.039044514298439026, 0.152852863073349, -0.0775560736656189, -0.057459451258182526, -0.0883132666349411, -0.05700721591711044, -0.0719662681221962, -0.17391231656074524, -0.15423375368118286, 0.011427801102399826, 0.23926392197608948, -0.09779452532529831, 0.07026837766170502, -0.10598921030759811, 0.10146301984786987, -0.13700595498085022, 0.08462251722812653, 0.012551739811897278, 0.00559019111096859, -0.14439387619495392, -0.0005218610167503357, 0.0979425460100174, 0.01739509031176567, -0.018917474895715714, 0.04538685083389282, 0.07335549592971802, 0.06400510668754578, -0.18341869115829468, -0.18660590052604675, -0.022345248609781265, -0.14622946083545685, 0.11155509948730469, 0.06769916415214539, 0.07929500937461853, -0.3453589081764221, 0.3338373899459839, -0.2123817801475525, 0.035468898713588715, -0.16212034225463867, -0.11163314431905746, -0.0063535613007843494, -0.03581070899963379, -0.07613731175661087, 0.18666449189186096, 0.00009885057806968689, -0.006334148347377777, 0.005300452467054129, -0.04616910591721535, -0.04830484837293625, -0.05174825340509415, 0.02732255309820175, 0.05137116461992264, -0.036110769957304, -0.11491215229034424, 0.14634057879447937, 0.02804866060614586, -0.02020830474793911, 0.025046728551387787, 0.10614261031150818, 0.14381222426891327, 0.0015136003494262695, 0.09443961828947067, 0.05388142168521881, 0.009988762438297272, 0.05514951050281525, 0.30501067638397217, -0.11709700524806976, 0.06722844392061234, 0.3389342129230499, -0.08832844346761703, -0.38282349705696106, -0.07121598720550537, 0.023716561496257782, 0.028655096888542175, -0.08030971884727478, -0.051625531166791916, 0.02732723020017147, -0.4469068646430969, 0.08823088556528091, -0.16075104475021362, -0.18134066462516785, -0.08321139216423035, -0.18355172872543335, -0.4385150372982025, 0.26805853843688965, 0.1917479932308197, 0.15937882661819458, 0.12445191293954849, 0.044761404395103455, 0.012186961248517036, -0.35358482599258423, -0.03821643069386482, 0.19263632595539093, 0.10109640657901764, 0.12826520204544067, 0.14154411852359772, -0.023231595754623413, -0.1392555832862854, -0.15909528732299805, -0.029807070270180702, -0.04009503126144409, 0.23696616291999817, 0.18518760800361633, -0.12097711116075516, 0.18846529722213745, 0.005675621330738068, 0.21166646480560303, -0.23663881421089172, -0.02330941893160343, 0.2590983211994171, 0.03822293505072594, 0.0999058410525322, 0.20029368996620178, -0.07495447248220444, 0.023572608828544617, -0.09588759392499924, -0.0626799464225769, 0.19109764695167542, -0.12411344051361084, -0.004987113177776337, -0.07659049332141876, 0.12666162848472595, -0.12095402181148529, 0.16513334214687347, 0.4998630881309509, 0.18022802472114563, -0.21747098863124847, 0.1472300887107849, 0.1630271077156067, -0.1997649222612381, 0.10519742965698242, -0.11286148428916931, 0.07656791806221008, 0.041628360748291016, 0.17732125520706177, 0.05188317596912384, -0.08270931243896484, 0.08062520623207092, -0.10116413235664368, 0.09392715245485306, 0.061662107706069946, -0.2262735515832901, 0.015810195356607437, -0.1122327521443367, -0.27529972791671753, -0.08161181211471558, 0.1334490329027176, 0.021770818158984184, -0.1146845668554306, 0.34687814116477966, 0.06746481359004974, -0.056389156728982925, -0.18747596442699432, -0.14660166203975677, -0.00648653507232666, -0.11136195063591003, -0.03596751391887665, 0.129851832985878, -0.14792576432228088, -0.29447194933891296, -0.04178891330957413, 0.2558356821537018, 0.07184064388275146, -0.14200301468372345, 0.09951627999544144, -0.33119043707847595, 0.189817413687706, -0.10268235206604004, -0.2747906446456909, -0.03890906646847725, 0.07342572510242462, -0.010098081082105637, -0.0818939283490181, -0.10090958327054977, -0.13902392983436584, 0.12152828276157379, 0.2991633117198944, 0.21057432889938354, 0.021368149667978287, -0.14141260087490082, 0.10492918640375137, -0.21407833695411682, -0.07362038642168045, -0.1761593073606491, 0.06258417665958405, -0.013353921473026276, -0.20457357168197632, -0.1448487937450409, -0.05052798241376877, -0.06178578361868858, 0.027579333633184433, 0.008653439581394196, 0.008720612153410912, 0.06566084921360016, -0.11319322884082794, -0.2029290348291397, 0.099524587392807, -0.08698567003011703, -0.07618158310651779, -0.03720507398247719, 0.003532666712999344, -0.0863618403673172, 0.02473323605954647, -0.06348276138305664, 0.08853932470083237, 0.18637952208518982, -0.04278401657938957, 0.1465344876050949, 0.021403715014457703, 0.16469745337963104, 0.02606629952788353, 0.06061801314353943, -0.03422984108328819, -0.0021168701350688934, 0.12526312470436096, 0.04261837899684906, -0.23438149690628052, 0.16509853303432465, 0.12427131086587906, -0.10334651172161102, -0.0015741735696792603, 0.20834267139434814, -0.019883938133716583, -0.05177135765552521, -0.15545256435871124, -0.3055613934993744, 0.0022050905972719193, 0.10184819996356964, 0.22812065482139587, 0.3006443977355957, 0.14379262924194336, 0.20196697115898132, 0.028025154024362564, 0.06293103098869324, 0.09665628522634506, -0.023409023880958557, 0.1401396095752716, -0.2525649666786194, -0.06917452812194824, 0.19264796376228333, -0.007144032046198845, 0.04496530443429947, -0.04547543823719025, 0.05764978006482124, -0.2319583296775818, 0.20810312032699585, -0.09786045551300049, -0.020545978099107742, 0.17913316190242767, 0.059770431369543076, 0.16313427686691284, 0.2975013852119446, 0.09258068352937698, -0.037485986948013306, 0.02677183970808983, 0.26272648572921753, 0.15684974193572998, -0.12550309300422668, 0.02974182739853859, 0.2328014373779297, 0.23415905237197876, 0.08057046681642532, 0.06822995096445084, -0.06851367652416229, -0.057259730994701385, -0.04409459978342056, -0.03695455193519592, -0.03736508637666702, 0.029561039060354233, -0.16999851167201996, 0.10658778995275497, 0.06138429418206215, 0.04294220358133316, -0.0267998818308115, 0.261827677488327, -0.1702384054660797, -0.24588550627231598, -0.08478766679763794, -0.016559645533561707, -0.04144167900085449, -0.007369711995124817, 0.02927962876856327, 0.014858275651931763, -0.1374569982290268, 0.11726885288953781, -0.009949233382940292, 0.11620084196329117, -0.14804653823375702, -0.04257692024111748, 0.4172683358192444, -0.19972515106201172, 0.17623165249824524, -0.12305854260921478, -0.027190789580345154, 0.16381508111953735, 0.06742619723081589, 0.18188762664794922, -0.1100442111492157, -0.1781599223613739, -0.20605294406414032, 0.001756414771080017, 0.06292090564966202, 0.1262967586517334, -0.06479938328266144, -0.05791494622826576, 0.025142312049865723, 0.022080156952142715, -0.2411768138408661, 0.09627708792686462, -0.0990896001458168, -0.049783848226070404, 0.046825554221868515, 0.15084269642829895, 0.06292109191417694, -0.14803549647331238, -0.0005782507359981537, -0.06737377494573593, -0.01587224006652832, 0.14934344589710236, 0.10123533010482788, 0.02296440303325653, 0.1212470531463623, 0.10476775467395782, -0.02799084223806858, 0.07409659773111343, -0.0003279632655903697, 0.01179206371307373, 0.11997032165527344, -0.12228082865476608, -0.16880837082862854, -0.016562022268772125, 0.049650534987449646, 0.07772587239742279, 0.078306645154953, 0.17585942149162292, -0.26580294966697693, 0.11791427433490753, 0.0926414504647255, -0.15276236832141876, -0.27332475781440735, 0.16871249675750732, 0.06693452596664429, 0.012942835688591003, 0.06512871384620667, 0.7710385918617249, 0.03476010262966156, -0.05438234284520149, 0.031800784170627594, 0.01698390394449234, 0.16600632667541504, -0.02557644620537758, -0.06399692595005035, -0.3203289210796356, -0.28306829929351807, -0.23654599487781525, -0.21201454102993011, -0.42575210332870483, 0.23422464728355408, -0.02554507553577423, 0.08012503385543823, 0.10630171000957489, 0.23465833067893982, -0.00885680876672268, -0.11128295212984085, 0.15803544223308563, 0.08748441934585571, 0.08620182424783707, -0.01395418494939804, 0.06725724041461945, 0.010562622919678688, -0.2012300044298172, -0.12223576009273529, 0.05564260482788086, -0.19198334217071533, -0.2871454954147339, -0.04254762455821037, 0.1926029920578003, -0.16490086913108826, -0.02337685227394104, -0.047614313662052155, 0.1430712342262268, -0.2876327633857727, -0.05702701583504677, -0.22326311469078064, -0.09926477074623108, 0.2974185347557068, -0.15853717923164368, 0.05398756265640259, -0.08026786148548126, 0.0921008437871933, 0.10672590881586075, -0.11007878184318542, 0.20669680833816528, 0.06881474703550339, 0.1346627175807953, 0.003686085343360901, -0.11832711100578308, -0.08530401438474655, -0.0753062292933464, -0.1503397673368454, -0.10006420314311981, 0.061880677938461304, -0.04806074500083923, -0.11540260910987854, 0.06095804274082184, 0.1824275255203247, 0.07023507356643677, 0.3528163433074951, 0.006628762930631638, 0.03179062902927399, 0.025397755205631256, 0.19250252842903137, -0.01655484549701214, -0.07142530381679535, -0.042004965245723724, -0.1362648904323578, -0.044794779270887375, -0.0035245902836322784, -0.22932179272174835, -0.07350026071071625, -0.0680881142616272, -0.05710294842720032, -0.05520443618297577, -0.0013919379562139511, -0.260537326335907, -0.1427590399980545, -0.09614921361207962, 0.20677970349788666, 0.07803855836391449, 0.012625575065612793, 0.029813747853040695, -0.1829834133386612, 0.1685183346271515, -0.16641245782375336, -6.837904464873645E-9, -0.1606673300266266, 0.061300456523895264, 0.1017630398273468, 0.32421958446502686, 0.10094517469406128, -0.1888313889503479, 0.1031997799873352, 0.05635581910610199, 0.20329326391220093, 0.05626673996448517, 0.0666680708527565, -0.11951874196529388, 0.33775848150253296, 0.01766863465309143, -0.002177940681576729, 0.02171327918767929, 0.144063338637352, -0.15215641260147095, -0.3108408749103546, -0.12132208049297333, 0.19141431152820587, -0.038771018385887146, -0.20757180452346802, 0.22296252846717834, 0.22188255190849304, 0.5921126008033752, 0.2096809297800064, 0.15638713538646698, -0.008383579552173615, 0.032230623066425323, 0.16115416586399078, 0.007566218264400959, 0.03659370541572571, 0.2538379430770874, 0.15507996082305908, 0.04193400219082832, -0.09517034888267517, 0.1235787644982338, -0.1968460977077484, 0.10803642123937607, 0.055189479142427444, -0.2626647353172302, 0.33596259355545044, -0.016702741384506226, -0.039606500416994095, -0.09091243147850037, 0.05999898910522461, -0.039872754365205765, -0.053760427981615067, -0.07895397394895554, -0.22027665376663208, -0.008431810885667801, -0.08953120559453964, -0.09138982743024826, -0.032754071056842804, 0.0790543407201767, -0.10871107876300812, 0.0646788626909256, -0.11245225369930267, -0.11236843466758728, -0.06795147061347961, 0.23829996585845947, 0.1768084466457367, -0.028132859617471695, -0.010708678513765335, 0.198024719953537, 0.16555750370025635, 0.043604686856269836, 0.09378010779619217, -0.08155347406864166, -0.20101197063922882, -0.0028049491811543703, 0.3718990981578827, 0.06677167117595673, 0.4967546761035919, 0.001818545162677765, -0.2540391683578491, -0.1965729296207428, -0.016673270612955093, -0.021598462015390396, -0.060269054025411606, 0.16706661880016327, -0.04050406068563461, 0.029570557177066803, -0.2966498136520386, -0.20449039340019226, 0.047098688781261444, -0.28289908170700073, -0.057702574878931046, -0.02650614082813263, -0.1202884316444397, -0.03157167136669159, -0.14892876148223877, 0.2866280972957611, -0.039919693022966385, 0.005492307245731354, -0.07316159456968307, -0.006280161440372467, 0.12898489832878113, -0.012477396056056023, -0.19903406500816345, -0.006130412220954895, -0.05219092592597008, -0.0823502391576767, -0.21326839923858643, 0.22728803753852844, -0.03914346918463707, 0.2863762378692627, 0.15412019193172455, 0.08509308099746704, 0.17792734503746033, -0.1323414146900177, 0.040831394493579865, 0.07748398929834366, -0.1683666855096817, 0.09577502310276031, 0.08958173543214798, -0.037761569023132324, 0.16343443095684052, 0.27119359374046326, 0.0887005403637886, 0.013173755258321762, -0.24546845257282257, -0.1570705622434616, -0.23577144742012024, 0.04665369540452957, 0.0940496101975441, -0.058363594114780426, 0.018420450389385223, -0.10470687597990036, 0.13388650119304657, -0.10326415300369263, 0.011635560542345047, 0.07671615481376648, -0.08816671371459961, 0.08786436915397644, 0.17060424387454987, -0.02847139537334442, -0.23939959704875946, 0.019784025847911835, 0.06927429884672165, 0.07224071025848389, 0.14901623129844666, -0.2510564923286438, -0.15502427518367767, -0.29774755239486694, -0.03928804397583008, 0.019119538366794586, -0.1888316571712494, 0.07856608182191849, -0.011183870024979115, 0.015122681856155396, -0.039441272616386414, 0.26433005928993225, 0.07809467613697052, -0.04332419112324715, -0.20887339115142822, 0.06536072492599487, 0.24243450164794922, -0.005855163559317589, -0.023386076092720032, -0.11065702885389328, -0.17672251164913177, 0.06256096065044403, -0.1250075250864029, 0.2128097414970398, 0.10627664625644684, 0.01802643947303295, -0.06510139256715775, 0.14439602196216583, -0.2692054510116577, -0.17843130230903625, 0.21154823899269104, 0.0000782795250415802, -0.059958502650260925, 0.21714675426483154, 0.027395717799663544, -0.01634279452264309, 0.1885353922843933, -0.2069086730480194, -0.3905485272407532, -0.23893095552921295, 0.17341268062591553, -0.016752643510699272, 0.23984763026237488, 0.010425429791212082, -0.1365075409412384, -0.15263302624225616, -0.052217502146959305, 0.11099378764629364, -0.026497848331928253, 0.16028937697410583, -0.06911518424749374, -0.14679275453090668, 0.23599092662334442, -0.01085161417722702, 0.0602710098028183, 0.17209580540657043, -0.10188084095716476, -0.0426529198884964, 0.08989007771015167, -0.7083189487457275, 0.14313946664333344, 0.001943991519510746, 0.23645536601543427, -0.03988301008939743, -0.18104103207588196, -0.04517760127782822, 0.012016820721328259, -0.07836496084928513, -0.11622727662324905, 0.018799614161252975, -0.027012094855308533, -0.3355201482772827, 0.30007925629615784, -0.09900832176208496, 0.14662544429302216, 0.17501702904701233, 0.0744343027472496, -0.12452244758605957, -0.02989521250128746, 0.06810130178928375, 0.054282255470752716};


//        glm.esClient.create(c -> c
//                .index(index)
//                .id("1")
//                .document(new EsText("hello", vector))
//        );


//        glm.retrieval(index, "embedding", vector);

//        glm.createList(index, Arrays.asList(
//                "死亡过于绝对，而活着，则有无限的可能。",
//                "幸福破灭之时，总是伴随着血腥味。",
//                "要优秀啊，不然怎么遇见优秀的人！",
//                "今天没有月亮，月亮在我怀里。",
//                "天街小雨润如酥，草色遥看近却无。",
//                "原来我们之间已没有任何关系。",
//                "感谢你特别邀请，来见证你的爱情",
//                "你像天外来物一样求之不得。",
//                "我不是你的爸爸，我是你的妈妈。",
//                "听妈妈的话，别让她受伤。",
//                "我好像在哪见过你",
//                "所以到哪里都像快乐被燃起",
//                "就好像你曾在我隔壁的班级"
//        ));
        List<Double> embedding = glm.textEmbedding("我在这见过你");
        log.info(embedding.toString());
    }

    // test
    public void createList(String index, List<String> texts) {
        texts.forEach(text -> {
            try {
                List<Double> embedding = textEmbedding(text);

                Elasticsearch.EsClient().create(c -> c
                        .index(index)
                        .id(UUID.randomUUID().toString())
                        .document(new EsText(text, embedding))
                );
            } catch (IOException e) {
                log.error("create index error: {}", e.getMessage());
            }
        });
    }

    @Data
    public static class EsText {
        private String text;
        private List<Double> embedding;

        public EsText(String text, List<Double> embedding) {
            this.text = text;
            this.embedding = embedding;
        }
    }
}
