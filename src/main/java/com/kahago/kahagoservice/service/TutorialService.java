package com.kahago.kahagoservice.service;

import com.google.gson.Gson;
import com.kahago.kahagoservice.component.FirebaseComponent;
import com.kahago.kahagoservice.entity.MCouponDiscountEntity;
import com.kahago.kahagoservice.entity.MTutorialEntity;
import com.kahago.kahagoservice.enummodel.BlastType;
import com.kahago.kahagoservice.enummodel.TutorialEnum;
import com.kahago.kahagoservice.exception.InternalServerException;
import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.dto.BlastDTO;
import com.kahago.kahagoservice.model.request.EditTutorialRequest;
import com.kahago.kahagoservice.model.request.ImageRequest;
import com.kahago.kahagoservice.model.request.NewTutorialRequest;
import com.kahago.kahagoservice.model.request.PromoRequest;
import com.kahago.kahagoservice.model.response.PromoRes;
import com.kahago.kahagoservice.model.response.SaveResponse;
import com.kahago.kahagoservice.model.response.TutorialBoResponse;
import com.kahago.kahagoservice.repository.MCouponDiscountRepo;
import com.kahago.kahagoservice.repository.MTutorialRepo;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.kahago.kahagoservice.util.ImageConstant.PREFIX_PATH_IMAGE_COUPON;
import static com.kahago.kahagoservice.util.ImageConstant.PREFIX_PATH_IMAGE_TUTORIAL;


/**
 * @author Hendro yuwono
 */
@Service
public class TutorialService {

    private static final int TYPE_PROMO = 2;
    private static final boolean ACTIVE_DASHBOARD = true;
    private static final int TYPE_TUTORIAL_WEB = 1;
    private static final int TYPE_TUTORIAL_MOBILE = 2;

    @Autowired
    private FirebaseComponent firebase;
    @Autowired
    private MTutorialRepo tutorialRepo;
    @Autowired
    private MCouponDiscountRepo mDiscountRepo;

    @Value("${kahago.image.tutorial}")
    private String pathDirectory;

    private static final Logger log = LoggerFactory.getLogger(TutorialService.class);

    public List<PromoRes> findPromo() {
        List<MTutorialEntity> promo = tutorialRepo.findByJenisTutorialOrderByStepAsc(TYPE_PROMO);

        if (promo.size() > 0) {
            return promo.stream().map(this::toDto).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    public List<PromoRes> getAll() {
        List<PromoRes> allpromo = new ArrayList<PromoRes>();
        List<MTutorialEntity> promo = tutorialRepo.findByJenisTutorialAndShowDashboardOrderByStepAsc(TYPE_PROMO, 1);
        List<MCouponDiscountEntity> coupon = mDiscountRepo.findByShowDashboard(ACTIVE_DASHBOARD);
        if (promo.size() > 0) {
            allpromo.addAll(promo.stream().map(this::toDto).collect(Collectors.toList()));
        }
        if (coupon.size() > 0) {
            allpromo.addAll(coupon.stream().map(this::toDtoCoupon).collect(Collectors.toList()));
        }

        return allpromo;
    }

    public PromoRes findById(Integer id) {
        return tutorialRepo.findById(id).map(this::toDto).orElseThrow(() -> new NotFoundException("Promo is not found"));
    }

    public List<PromoRes> getAllTutorial() {
        List<MTutorialEntity> ltutor = tutorialRepo.findAllOrderByStepAsc();
        if (ltutor.size() > 0) {
            return ltutor.stream().map(this::toDto).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    private PromoRes toDto(MTutorialEntity entity) {
        String pathImage = PREFIX_PATH_IMAGE_TUTORIAL + entity.getPathImage().substring(entity.getPathImage().lastIndexOf("/") + 1);
        String pathImageBack = null;
        String imageBlast = null;
        if (entity.getPathImageBack() != null) {
            pathImageBack = PREFIX_PATH_IMAGE_TUTORIAL + entity.getPathImageBack().substring(entity.getPathImageBack().lastIndexOf("/") + 1);
        }
        if (entity.getPathBlastImage() != null) {
            imageBlast = PREFIX_PATH_IMAGE_TUTORIAL + entity.getPathBlastImage().substring(entity.getPathBlastImage().lastIndexOf("/") + 1);
        }

        return PromoRes.builder()
                .seqId(entity.getSeqid())
                .step(entity.getStep())
                .description(entity.getDescription())
                .typeDasboard(1)
                .urlImage(pathImage)
                .urlImageDetail(pathImageBack == null ? pathImage : pathImageBack)
                .urlImageBlast(imageBlast == null ? imageBlast : imageBlast)
                .build();
    }

    private PromoRes toDtoCoupon(MCouponDiscountEntity entity) {
        String pathImage = PREFIX_PATH_IMAGE_COUPON + entity.getUrlFrontImage().substring(entity.getUrlFrontImage().lastIndexOf("/") + 1);
        String pathImageBack = PREFIX_PATH_IMAGE_COUPON + entity.getUrlBackgroundImage().substring(entity.getUrlBackgroundImage().lastIndexOf("/") + 1);
        return PromoRes.builder()
                .seqId(entity.getId())
                .step(entity.getId())
                .description(entity.getDescription())
                .typeDasboard(1)
                .urlImage(pathImage)
                .urlImageDetail(pathImageBack)
                .build();
    }

    @Transactional(rollbackOn = Exception.class)
    public SaveResponse savePromo(PromoRequest request) {
        MTutorialEntity entity = new MTutorialEntity();
        entity.setPromoName(request.getPromoName());
        entity.setStep(request.getStep());
        entity.setJenisTutorial(TutorialEnum.DASHBOARD_ANDROID.getNumber());
        entity.setDescription(request.getDescription() == null ? "" : request.getDescription());
        if (request.getImagePath() == null) {
            throw new NotFoundException("Image tidak boleh kosong !");
        }
        if (request.getImageBack() == null) {
            throw new NotFoundException("Image Back tidak boleh kosong !");
        }
        if (request.getImageBlast() == null) {
            throw new NotFoundException("Image Blast tidak boleh kosong !");
        }
        Integer show = 0;
        if (request.getShowDashboard()) {
            show = 1;
        }
        entity.setShowDashboard(show);
        entity.setPathImage(uploadFile(request.getImagePath(), "step-", String.valueOf(TutorialEnum.DASHBOARD_ANDROID.getNumber()), "-", request.getImagePath().getFileName()));
        entity.setPathImageBack(uploadFile(request.getImageBack(), "step-", String.valueOf(TutorialEnum.DASHBOARD_ANDROID.getNumber()), "-", request.getStep() + "-b"));
        entity.setPathBlastImage(uploadFile(request.getImageBlast(), "blast-", String.valueOf(TutorialEnum.DASHBOARD_ANDROID.getNumber()), "-", request.getStep() + "-b"));
        entity.setLastUpdate(LocalDateTime.now());
        entity.setLastUser("admin");
        tutorialRepo.save(entity);

        return SaveResponse.builder()
                .saveInformation("Berhasil Simpan Promo")
                .saveStatus(1)
                .build();
    }

    @Transactional(rollbackOn = Exception.class)
    public SaveResponse saveEdit(PromoRequest request) {
        MTutorialEntity entity = tutorialRepo.findBySeqid(request.getSeqid());
        entity.setPromoName(request.getPromoName());
        entity.setPromoName(request.getPromoName());
        entity.setStep(request.getStep());
        entity.setJenisTutorial(TutorialEnum.DASHBOARD_ANDROID.getNumber());
        entity.setDescription(request.getDescription() == null ? "" : request.getDescription());
        if (request.getImagePath() != null) {
            entity.setPathImage(uploadFile(request.getImagePath(), "step-", String.valueOf(TutorialEnum.DASHBOARD_ANDROID.getNumber()), "-", request.getImagePath().getFileName()));
        }
        if (request.getImageBack() != null) {
            entity.setPathImageBack(uploadFile(request.getImageBack(), "step-", String.valueOf(TutorialEnum.DASHBOARD_ANDROID.getNumber()), "-", request.getStep() + "-b"));
        }
        if (request.getImageBlast() != null) {
            entity.setPathBlastImage(uploadFile(request.getImageBlast(), "blast-", String.valueOf(TutorialEnum.DASHBOARD_ANDROID.getNumber()), "-", request.getStep() + "-b"));
        }
        entity.setLastUpdate(LocalDateTime.now());
        entity.setLastUser("admin");
        tutorialRepo.save(entity);

        return SaveResponse.builder()
                .saveInformation("Berhasil Edit Promo")
                .saveStatus(1)
                .build();
    }

    private String uploadFile(ImageRequest req, String kode, String jenisTutorial, String kodebl, String name) {
        String path = "";
        try {
            byte[] bytes = Base64.decodeBase64(req.getContent());
            path = pathDirectory + kode + jenisTutorial + kodebl + name + ".png";
            Path fileLoc = Paths.get(path);
            Files.write(fileLoc, bytes);
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new InternalServerException(e.getMessage());
        }
        return path;
    }

    @Transactional(rollbackOn = Exception.class)
    public SaveResponse deletePromo(Integer seqid) {
        MTutorialEntity promo = tutorialRepo.findBySeqid(seqid);
        if (promo == null) {
            throw new NotFoundException("Data Tidak Ditemukan !");
        }
        tutorialRepo.delete(promo);
        return SaveResponse.builder()
                .saveInformation("Berhasil Hapus Promo")
                .saveStatus(1)
                .build();
    }

    @org.springframework.transaction.annotation.Transactional
    public void save(NewTutorialRequest request, String userLoginName) {
        int id = lastTutorialId() + 1;

        MTutorialEntity entity = MTutorialEntity.builder()
                .seqid(id)
                .step(request.getStep())
                .promoName(request.getPromoName())
                .pathImage(saveImageToDirectory(request.getByteImageFront(), "front_"))
                .pathImageBack(saveImageToDirectory(request.getByteImageBackground(), "background_"))
                .pathBlastImage(saveImageToDirectory(request.getByteImagePopup(), "popup_"))
                .jenisTutorial(request.getTypeOfTutorial())
                .description(request.getDescription())
                .lastUser(userLoginName)
                .lastUpdate(LocalDateTime.now())
                .showDashboard(request.isShowInDashboard() ? 1 : 0)
                .build();

        tutorialRepo.save(entity);
    }

    private int lastTutorialId() {
        return tutorialRepo.findFirstByOrderBySeqidDesc().map(MTutorialEntity::getSeqid)
                .orElseThrow(() -> new NotFoundException("Tutorial is not found"));
    }

    private String saveImageToDirectory(byte[] imageByte, String prefix) {
        try {
            String pathLocation = pathDirectory + prefix + RandomStringUtils.randomAlphanumeric(35) + ".jpg";
            Path fileLoc = Paths.get(pathLocation);
            Files.write(fileLoc, imageByte);

            return pathLocation;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void saveEdit(String updateWho, Integer id, EditTutorialRequest request) {
        MTutorialEntity entity = tutorialRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Tutorial with id " + id + " is not found"));

        validationAndFillRequest(entity, request, updateWho);
        tutorialRepo.save(entity);
    }

    private void validationAndFillRequest(MTutorialEntity entity, EditTutorialRequest request, String updateWho) {
        if (request.getStep() != 0) {
            entity.setStep(request.getStep());
        }
        if (!request.getPromoName().isEmpty()) {
            entity.setPromoName(request.getPromoName());
        }
        if (!request.getDescription().isEmpty()) {
            entity.setPromoName(request.getDescription());
        }
        if (request.getTypeOfTutorial() != null) {
            entity.setJenisTutorial(request.getTypeOfTutorial());
        }
        if (!Objects.isNull(request.getImageBackground())) {
            entity.setPathImageBack(saveImageToDirectory(request.getByteImageBackground(), "background_"));
        }
        if (!Objects.isNull(request.getImageFront())) {
            entity.setPathImage(saveImageToDirectory(request.getByteImageFront(), "front_"));
        }
        if (!Objects.isNull(request.getImagePopup())) {
            entity.setPathBlastImage(saveImageToDirectory(request.getByteImagePopup(), "popup_"));
        }
        if (request.getShowInDashboard() != null) {
            entity.setShowDashboard(request.getShowInDashboard() ? 1 : 0);
        }
        entity.setLastUser(updateWho);
        entity.setLastUpdate(LocalDateTime.now());
    }

    public Page<TutorialBoResponse> fetchAll(String type, Pageable pageable) {
        Integer typeOfTutorial = filterTypeOfTutorial(type);
        Page<MTutorialEntity> allTutorial = tutorialRepo.findByJenisTutorialOrderByStepAsc(typeOfTutorial, pageable);

        return new PageImpl<>(
                allTutorial.getContent().stream().map(this::toTutorialBo).collect(Collectors.toList()),
                allTutorial.getPageable(),
                allTutorial.getTotalElements()
        );
    }

    private int filterTypeOfTutorial(String type) {
        if (type.equals("WEB")) {
            return TYPE_TUTORIAL_WEB;
        } else {
            return TYPE_TUTORIAL_MOBILE;
        }
    }

    private TutorialBoResponse toTutorialBo(MTutorialEntity entity) {
        boolean showDashboard = entity.getShowDashboard() != null && entity.getShowDashboard() == 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy mm:ss");
        return TutorialBoResponse.builder()
                .id(entity.getSeqid())
                .step(entity.getStep())
                .pathImageFront(reversePathToUrl(entity.getPathImage()))
                .pathImageBackground(reversePathToUrl(entity.getPathImageBack()))
                .pathImagePopup(reversePathToUrl(entity.getPathBlastImage()))
                .typeOfTutorial(entity.getJenisTutorial())
                .description(entity.getDescription())
                .showInDashboard(showDashboard)
                .promoName(entity.getPromoName())
                .lastModifyBy(entity.getLastUser())
                .lastUpdate(entity.getLastUpdate().format(formatter))
                .build();
    }

    public TutorialBoResponse get(Integer id) {
        return tutorialRepo.findById(id).map(this::toTutorialBo)
                .orElseThrow(() -> new NotFoundException("Tutorial with id " + id + " is not found"));
    }

    private String reversePathToUrl(String localPath) {
        if (localPath == null) {
            return "";
        }
        return PREFIX_PATH_IMAGE_TUTORIAL + localPath.substring(localPath.lastIndexOf("/") + 1);
    }

    public void blastTutorial(Integer id) {
        MTutorialEntity entity = tutorialRepo.findById(id).orElseThrow(() -> new NotFoundException("Tutorial With Id " + id + " is not found"));
        BlastDTO blastDTO = BlastDTO.builder()
                .title(entity.getPromoName())
                .description(entity.getDescription())
                .idBlast(entity.getSeqid().toString())
                .imageLocation("/blast/" + BlastType.TUTORIAL.toString() + "/" + entity.getSeqid().toString() + entity.getPathBlastImage().substring(entity.getPathBlastImage().lastIndexOf(".")))
                .typeBlast(BlastType.TUTORIAL.getInteger())
                .build();
        Gson gson = new Gson();
        JSONObject data = new JSONObject();
        try {
            String topik = "Blast";
            topik = "infodev";
            data.put("idTrx", "blast");
            data.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            data.put("userid", "-");
            data.put("nominal", "0");
            data.put("type_trx", "blast"); //1. Book, 2. Deposit, 3.news
            data.put("tittle", "blast");
            data.put("tag", "blast");
            data.put("body", gson.toJson(blastDTO));
            data.put("status_trx", "1"); //0.failed, 1.Success
            firebase.notifAll("Blast", "Blast Promo Terbaru", data, "Blast",topik);
        }catch (Exception e) {
            // TODO: handle exception
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
