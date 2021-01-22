package com.kahago.kahagoservice.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kahago.kahagoservice.entity.*;
import com.kahago.kahagoservice.model.response.*;
import com.kahago.kahagoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kahago.kahagoservice.exception.NotFoundException;
import com.kahago.kahagoservice.model.request.MenuDetailReq;
import com.kahago.kahagoservice.model.request.MenuParentReq;
import com.kahago.kahagoservice.model.request.MenuSettingRequest;
import com.kahago.kahagoservice.model.request.OrderNumberMenuRequest;


@Service
public class MenuService {
    @Autowired
    private MGroupListRepo groupListRepo;
    @Autowired
    private MMenuHeaderRepo mnHeaderRepo;
    @Autowired
    private MUserRepo userRepo;
    @Autowired
    private MUserCategoryRepo mUserCategoryRepo;
    @Autowired
    private MMenuTitleRepo menuTitleRepo;

    @Autowired
    private MMenuRepo menuRepo;

    public List<MenuDetails> findAllMenu(String userid) {
        // TODO Auto-generated method stub
        MUserEntity user = userRepo.findById(userid).get();
        List<MenuDetails> lsMenuDetail = new ArrayList<MenuDetails>();
        List<MGroupListEntity> lsGroup = groupListRepo.findAllByUserCategory(user.getUserCategory().getSeqid());
        Map<Integer, List<MGroupListEntity>> lsMapGroup = lsGroup.
                stream().collect(Collectors.groupingBy(MGroupListEntity::getMenuParentId, Collectors.toList()));
        for (Entry<Integer, List<MGroupListEntity>> ml : lsMapGroup.entrySet()) {
            MenuDetails menu = MenuDetails.builder()
                    .idParent(ml.getValue().get(0).getMenuId().getMenuParentId())
                    .listChild(ml.getValue().stream().map(this::getMenuList).collect(Collectors.toList()))
                    .build();
            lsMenuDetail.add(menu);
        }
        return lsMenuDetail;
    }

    private MenuList getMenuList(MGroupListEntity group) {
        MenuList menuList = toDtoMenuList(group.getMenuId());
        menuList.setIsDelete(group.getIsDelete());
        menuList.setIsRead(group.getIsRead());
        menuList.setIsWrite(group.getIsWrite());
        return menuList;
    }

    private MenuList getMenuList(MGroupListEntity group, Integer menuId) {
        MMenuEntity menu = menuRepo.findByMenuId(menuId);
        return MenuList.builder()
                .menuId(group == null ? menu.getMenuId() : group.getMenuId().getMenuId())
                .menuName(group == null ? menu.getMenuName() : group.getMenuId().getMenuName())
                .isDelete(group == null ? false : group.getIsDelete())
                .isRead(group == null ? false : group.getIsRead())
                .isWrite(group == null ? false : group.getIsWrite())
                .build();
    }

    public MenuList getPrivelage(String userId, Integer menuId) {
        MUserEntity user = userRepo.findById(userId).get();
        MGroupListEntity entity = groupListRepo.findByMenuIdMenuIdAndUserCategory(menuId, user.getUserCategory().getSeqid());
        return getMenuList(entity, menuId);
    }

    public List<MenuParentList> getMenuHeader() {
        return mnHeaderRepo.findAll().stream().map(this::toMenuHeader).collect(Collectors.toList());
    }

    public MenuParentList doSave(MenuParentReq menu) {
        MMenuParentEntity mn = MMenuParentEntity.builder()
                .menuParentName(menu.getMenuHeaderName())
                .idBadge(menu.getBadge()).build();
        mnHeaderRepo.save(mn);
        return toMenuHeader(mn);
    }

    private MenuParentList toMenuHeader(MMenuParentEntity menu) {
        return MenuParentList.builder()
                .menuHeaderId(menu.getMenuParentId())
                .menuHeaderName(menu.getMenuParentName())
                .badge(menu.getIdBadge()).build();
    }

    public SaveResponse saveMenuAccess(MenuSettingRequest request) {
        MUserCategoryEntity usercategory = mUserCategoryRepo.findBySeqid(request.getUserCategory());
        for (MenuDetailReq md : request.getMenu()) {
            MMenuEntity menu = menuRepo.findByMenuId(md.getMenuId());
            MGroupListEntity group = groupListRepo.findByMenuIdMenuIdAndUserCategory(md.getMenuId(), usercategory.getSeqid());
            if (menu == null) {
                throw new NotFoundException("Menu tidak ditemukan");
            }
            if (group == null) {
                group = new MGroupListEntity();
                group.setUserCategory(usercategory.getSeqid());
                group.setMenuId(menu);
                group.setIsDelete(md.getIsDelete());
                group.setIsRead(md.getIsRead());
                group.setIsWrite(md.getIsWrite());
            } else {
                group.setIsDelete(md.getIsDelete());
                group.setIsRead(md.getIsRead());
                group.setIsWrite(md.getIsWrite());
            }
            groupListRepo.save(group);
        }

        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil Update Akses Menu")
                .build();
    }

    @Transactional
    public SaveResponse createOrEditTitle(MenuTitle menuTitle, String userId) {
        MMenuTitleEntity mMenuTitle;
        if (menuTitle.getIdMenuTitle() == null) {
            mMenuTitle = new MMenuTitleEntity();
            mMenuTitle.setCreateBy(userId);
            mMenuTitle.setUpdateDate(LocalDateTime.now());
            mMenuTitle.setCreateDate(LocalDateTime.now());
        } else {
            mMenuTitle = menuTitleRepo.getOne(menuTitle.getIdMenuTitle());
            mMenuTitle.setUpdateBy(userId);
            mMenuTitle.setUpdateDate(LocalDateTime.now());
        }
        mMenuTitle.setOrderNumber(10);
        mMenuTitle.setStatus(true);
        menuTitle.setOrderNumber(10);
        mMenuTitle.setTitle(menuTitle.getSection());
        this.menuTitleRepo.save(mMenuTitle);
        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil update data menu title")
                .build();
    }

    @Transactional
    public SaveResponse createOrEditMenuParent(MenuDetails details, String userId) {
        MMenuParentEntity menuParentEntity;
        if (details.getIdParent() == null) {
            menuParentEntity = new MMenuParentEntity();
            menuParentEntity.setCreateBy(userId);
            menuParentEntity.setCreateDate(LocalDateTime.now());
            menuParentEntity.setUpdateDate(LocalDateTime.now());
        } else {
            menuParentEntity = mnHeaderRepo.getOne(details.getIdParent());
            menuParentEntity.setUpdateBy(userId);
            menuParentEntity.setUpdateDate(LocalDateTime.now());
        }
        menuParentEntity.setOrderNumber(10);
        menuParentEntity.setIcon(details.getIcon());
        menuParentEntity.setMenuParentName(details.getTitle());
        menuParentEntity.setIdMenuTitle(details.getTitleId());
        menuParentEntity.setPageUrl(details.getPage());
        mnHeaderRepo.save(menuParentEntity);
        if (details.getIdParent() == null) {
            MMenuEntity menu = new MMenuEntity();
            menu.setCreateBy(userId);
            menu.setCreateDate(LocalDateTime.now());
            menu.setUpdateDate(LocalDateTime.now());
            menu.setBonew(1);
            menu.setOrderNumber(0);
            menu.setUpdateBy(userId);
            menu.setMenuName(details.getMenuParentName());
            menu.setMenuParentId(menuParentEntity.getMenuParentId());
            menuRepo.save(menu);
        }
        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil update data menu Parent")
                .build();
    }

    @Transactional
    public SaveResponse createOrEditListMenu(MenuList menuList, String userId) {
        MMenuEntity menuEntity = null;
        if (menuList.getMenuId() == null) {
            menuEntity = new MMenuEntity();
            menuEntity.setCreateBy(userId);
            menuEntity.setCreateDate(LocalDateTime.now());
        } else {
            menuEntity = menuRepo.getOne(menuList.getMenuId());
            menuEntity.setUpdateBy(userId);
            menuEntity.setUpdateDate(LocalDateTime.now());
        }
        menuEntity.setBonew(1);
        menuEntity.setIcon(menuList.getIcon());
        menuEntity.setMenuName(menuList.getTitle());
        menuEntity.setPageUrl(menuList.getPage());
        menuEntity.setShowInMenu(menuList.getShowInMenu());
        menuEntity.setOrderNumber(menuList.getOrderNumber());
        menuEntity.setFlag(menuList.getFlag());
        menuEntity.setMenuParentId(menuList.getParentId());
        menuRepo.save(menuEntity);
        return SaveResponse.builder()
                .saveStatus(1)
                .saveInformation("Berhasil update data menu Detail")
                .build();
    }

    public List<MenuDetails> findAllMenuByUserCategory(Integer userCategory) {
        // TODO Auto-generated method stub
        List<MenuDetails> lsMenuDetail = new ArrayList<MenuDetails>();
        List<MGroupListEntity> lsGroup = groupListRepo.findAllByUserCategory(userCategory);
        Map<Integer, List<MGroupListEntity>> lsMapGroup = lsGroup.
                stream().collect(Collectors.groupingBy(MGroupListEntity::getMenuParentId, Collectors.toList()));
        for (Entry<Integer, List<MGroupListEntity>> ml : lsMapGroup.entrySet()) {
            MenuDetails menu;
            menu = this.toDtoMenuDetailWithoutChild(mnHeaderRepo.getOne(ml.getValue().get(0).getMenuId().getMenuParentId()));
            menu.setListChild(ml.getValue().stream().map(this::getMenuList).collect(Collectors.toList()));
            menu.getListChild().sort(Comparator.comparing(MenuList::getOrderNumber));
            lsMenuDetail.add(menu);
        }
        lsMenuDetail.sort(Comparator.comparing(MenuDetails::getOrderNumber));
        return lsMenuDetail;
    }

    public List<MenuTitle> getAllMenuPermissionByUserCategory(String userName) {
        MUserEntity mUserEntity = userRepo.getOne(userName);
        return getMenuByUserCategory(mUserEntity.getUserCategory().getSeqid());
    }

    public List<MenuTitle> getMenuByUserCategory(Integer idCategory) {
        List<MenuTitle> menuTitleArrayList = new ArrayList<MenuTitle>();
        List<MenuDetails> menuDetailsList = this.findAllMenuByUserCategory(idCategory);
        Map<Integer, List<MenuDetails>> lsMapGroup = menuDetailsList.stream().collect(Collectors.groupingBy(MenuDetails::getTitleId, Collectors.toList()));
        for (Entry<Integer, List<MenuDetails>> md : lsMapGroup.entrySet()) {
            MMenuTitleEntity mMenuTitle = menuTitleRepo.getOne(md.getValue().get(0).getTitleId());
            MenuTitle menuTitle = this.toDtoMenuTitleWithoutChild(mMenuTitle);
            menuTitle.setSubmenu(md.getValue());
            menuTitleArrayList.add(menuTitle);
        }
        menuTitleArrayList.sort(Comparator.comparing(MenuTitle::getOrderNumber));
        return menuTitleArrayList;
    }

    public List<MenuTitle> getAllMenuTitleWithPermission(Integer idCategory) {
        List<MenuTitle> menuTitleArrayList = this.getMenuByUserCategory(idCategory);
        List<MenuTitle> masterMenu = getAllMenuPermission();
        masterMenu.forEach(menuTitle -> {
            List<MenuTitle> menuTitle1s = menuTitleArrayList.stream()
                    .filter(menuTitle1 -> menuTitle1.getIdMenuTitle().equals(menuTitle.getIdMenuTitle())).collect(Collectors.toList());
            if (menuTitle1s.size()>0) {
                MenuTitle menuTitleMose= menuTitle1s.get(0);
                menuTitle.getSubmenu().forEach(details -> {
                    List<MenuDetails> menuDetails = menuTitleMose.getSubmenu().stream().filter(details1 -> details1.getIdParent().equals(details.getIdParent())).collect(Collectors.toList());
                    if (menuDetails.size()>0) {
                        details.getListChild().forEach(menuList -> {
                            List<MenuList> menuList1s = menuDetails.get(0).getListChild().stream().filter(menuList1 -> menuList.getMenuId().equals(menuList1.getMenuId())).collect(Collectors.toList());
                            if (menuList1s.size()>0) {
                                menuList.setIsRead(menuList1s.get(0).getIsRead());
                                menuList.setIsDelete(menuList1s.get(0).getIsDelete());
                                menuList.setIsWrite(menuList1s.get(0).getIsWrite());
                            }
//
//                    menuList1s.setIsWrite(menuList.getIsWrite());
//                    menuList1s.setIsDelete(menuList.getIsDelete());
//                    menuList1s.setIsRead(menuList.getIsRead());

                        });
                    }
                });
            }

        });
//        for (MenuTitle menuTitle : menuTitleArrayList) {
//            menuTitle.getSubmenu().forEach(details -> {
//                details.getListChild().stream()
//            });
//        }
        return masterMenu;
    }

    public List<MenuTitle> getPermissionMenuAccess(String userName) {
        List<MenuTitle> menuTitleArrayList = getAllMenuPermissionByUserCategory(userName);
        menuTitleArrayList.forEach(menuTitle -> {
            menuTitle.getSubmenu().forEach(details -> {
                details.setListChild(
                        details.getListChild().stream()
                                .filter(MenuList::getShowInMenu)
                                .collect(Collectors.toList())
                );
            });
        });

        return menuTitleArrayList;
    }

    public List<MenuTitle> getAllMenuPermission() {
        List<MenuTitle> lsMenuTitle;
        List<MMenuTitleEntity> listData = menuTitleRepo.findAll();
        lsMenuTitle = listData.stream().map(this::toDtoMenuTitle).collect(Collectors.toList());
        lsMenuTitle.sort(Comparator.comparing(MenuTitle::getOrderNumber));
        return lsMenuTitle;
    }

    private MenuTitle toDtoMenuTitle(MMenuTitleEntity menuTitle) {
        List<MenuDetails> menuDetailsList = menuTitle.getParentEntities().stream().map(this::toDtoMenuDetail).collect(Collectors.toList());
        menuDetailsList.sort(Comparator.comparing(MenuDetails::getOrderNumber));
        return MenuTitle.builder()
                .idMenuTitle(menuTitle.getId())
                .section(menuTitle.getTitle())
                .submenu(menuDetailsList)
                .orderNumber(menuTitle.getOrderNumber())
                .build();
    }

    private MenuTitle toDtoMenuTitleWithoutChild(MMenuTitleEntity menuTitle) {
        return MenuTitle.builder()
                .section(menuTitle.getTitle())
                .orderNumber(menuTitle.getOrderNumber())
                .idMenuTitle(menuTitle.getId())
                .build();
    }

    private MenuDetails toDtoMenuDetail(MMenuParentEntity parentEntity) {
        List<MenuList> listChild = parentEntity.getMenus().stream().map(this::toDtoMenuList).collect(Collectors.toList());
        listChild.sort(Comparator.comparing(MenuList::getOrderNumber));
        return MenuDetails.builder()
                .titleId(parentEntity.getIdMenuTitle())
                .menuParentName(parentEntity.getMenuParentName())
                .title(parentEntity.getMenuParentName())
                .idParent(parentEntity.getMenuParentId())
                .orderNumber(parentEntity.getOrderNumber())
                .icon(parentEntity.getIcon())
                .page(parentEntity.getPageUrl())
                .listChild(listChild)
                .build();
    }

    private MenuDetails toDtoMenuDetailWithoutChild(MMenuParentEntity parentEntity) {
        return MenuDetails.builder()
                .titleId(parentEntity.getIdMenuTitle())
                .menuParentName(parentEntity.getMenuParentName())
                .title(parentEntity.getMenuParentName())
                .idParent(parentEntity.getMenuParentId())
                .icon(parentEntity.getIcon())
                .orderNumber(parentEntity.getOrderNumber())
                .page(parentEntity.getPageUrl())
                .build();
    }

    private MenuList toDtoMenuList(MMenuEntity menuEntity) {
        return MenuList.builder()
                .parentId(menuEntity.getMenuParentId())
                .flag(menuEntity.getFlag())
                .orderNumber(menuEntity.getOrderNumber())
                .icon(menuEntity.getIcon())
                .menuId(menuEntity.getMenuId())
                .menuName(menuEntity.getMenuName())
                .isDelete(false)
                .isRead(false)
                .isWrite(false)
                .page(menuEntity.getPageUrl())
                .showInMenu(menuEntity.getShowInMenu())
                .title(menuEntity.getMenuName())
                .build();
    }
    @Transactional
    public SaveResponse saveOrderNumberTitle(MenuSettingRequest request,String userId) {
    	if(request.getOrderNumber() != null) {
    		for(OrderNumberMenuRequest orderNumb : request.getOrderNumber()) {
    			MMenuTitleEntity entity = menuTitleRepo.findById(orderNumb.getMenuIdTitle()).orElseThrow(()->new NotFoundException("Menu Title Tidak Ditemukan !"));
    			entity.setOrderNumber(orderNumb.getOrderNumber());
    			entity.setUpdateDate(LocalDateTime.now());
    			entity.setUpdateBy(userId);
    			menuTitleRepo.save(entity);
    		}
    	}
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Update Order Number Menu Title")
    			.build();
    }
    @Transactional
    public SaveResponse saveOrderNumberParent(MenuSettingRequest request,String userId) {
    	if(request.getOrderNumber() != null) {
    		for(OrderNumberMenuRequest orderNumb : request.getOrderNumber()) {
    			MMenuParentEntity parentEntity = mnHeaderRepo.findById(orderNumb.getMenuIdParent()).orElseThrow(()->new NotFoundException("Menu Parent Tidak Ditemukan !"));
    			parentEntity.setOrderNumber(orderNumb.getOrderNumber());
    			parentEntity.setUpdateBy(userId);
    			parentEntity.setUpdateDate(LocalDateTime.now());
    			mnHeaderRepo.save(parentEntity);
    		}
    	}
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Update Order Number Menu Parent")
    			.build();
    }
    @Transactional
    public SaveResponse saveOrderNumberMenu(MenuSettingRequest request,String userId) {
    	if(request.getOrderNumber() != null) {
    		for(OrderNumberMenuRequest orderNumb : request.getOrderNumber()) {
    			MMenuEntity menuEntity = menuRepo.findById(orderNumb.getMenuId()).orElseThrow(()->new NotFoundException("Menu Parent Tidak Ditemukan !"));
    			menuEntity.setOrderNumber(orderNumb.getOrderNumber());
    			menuEntity.setUpdateBy(userId);
    			menuEntity.setUpdateDate(LocalDateTime.now());
    			menuRepo.save(menuEntity);
    		}
    	}
    	return SaveResponse.builder()
    			.saveStatus(1)
    			.saveInformation("Berhasil Update Order Number Menu ")
    			.build();
    }
}
