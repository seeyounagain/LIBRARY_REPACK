package com.potato.project.admin.controller;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.potato.project.admin.service.AdminService;
import com.potato.project.common.service.CommonService;
import com.potato.project.common.util.UploadUtil;
import com.potato.project.common.vo.BookImgVO;
import com.potato.project.common.vo.BookVO;
import com.potato.project.common.vo.MenuVO;
import com.potato.project.content.service.BoardService;
import com.potato.project.content.service.SearchService;
import com.potato.project.member.vo.MemberVO;

@Controller
@RequestMapping("/libManage2")
public class Admin2Controller {
	
	@Resource(name = "adminService")
	private AdminService adminSerivce;
	
	@Resource(name = "searchService")
	private SearchService searchService;
	
	@Resource(name = "commonService")
	private CommonService commonService;
	
	// 도서관리 페이지 이동
	@GetMapping("/bookManage")
	public String bookManage(Model model,MenuVO menuVO,HttpSession session,BookVO bookVO) {
		
		// 메뉴 호출할 정보 가져오기
		MemberVO loginInfo = (MemberVO)session.getAttribute("loginInfo");
		
		if (loginInfo == null) {
			
			loginInfo = new MemberVO();
			
		}

		// 메뉴 전달
		model.addAttribute("menuList",commonService.selectMenuList(loginInfo));
		// 사이드 메뉴 전달
		model.addAttribute("sideMenuList",commonService.selectSideMenuList(menuVO));
		// 도서 목록 전달
		model.addAttribute("bookList",searchService.selectStatusBookList(bookVO));
		
		return  "admin/book_manage";
		
	}
	
	// 회원 관리 페이지 이동
	@GetMapping("/memberManage")
	public String memberManage(Model model,MenuVO menuVO,HttpSession session) {
		
		// 메뉴 호출할 정보 가져오기
		MemberVO loginInfo = (MemberVO)session.getAttribute("loginInfo");
		
		if (loginInfo == null) {
			
			loginInfo = new MemberVO();
			
		}
		// 메뉴 전달
		model.addAttribute("menuList",commonService.selectMenuList(loginInfo));
		// 사이드 메뉴 전달
		model.addAttribute("sideMenuList",commonService.selectSideMenuList(menuVO));
		
		return  "admin/member_manage";
		
	}
	
	// Ajax 사용, 상태별 도서 조회
	@ResponseBody
	@PostMapping("/selectStatusBookListAjax")
	public List<BookVO> selectStatusBookListAjax(BookVO bookVO) {
		
		return searchService.selectStatusBookList(bookVO);
		
	}
	
	// 도서 등록 페이지
	@GetMapping("/regBookForm")
	public String regBookForm(Model model,MenuVO menuVO,HttpSession session) {

		// 메뉴 호출할 정보 가져오기
		MemberVO loginInfo = (MemberVO)session.getAttribute("loginInfo");
				
			if (loginInfo == null) {
					
				loginInfo = new MemberVO();
					
			}
			
		// 메뉴 전달
		model.addAttribute("menuList",commonService.selectMenuList(loginInfo));
		// 사이드 메뉴 전달
		model.addAttribute("sideMenuList",commonService.selectSideMenuList(menuVO));	
		
		return "admin/reg_book_form";
		
	}
	
	// 도서 등록
	@PostMapping("/regBook")
	public String regBook(BookVO bookVO,MultipartHttpServletRequest multi) {
		
		// 첨부파일 UPLOAD
		
		// 첨부된 파일명 가져오기
		MultipartFile file = multi.getFile("file"); 
				
		// 파일이 첨부될 경로 (끝에 \\ 있는지 체크!)
		String uploadPath = "C:\\Users\\siyoon\\git\\TEAM_POTATO\\src\\main\\webapp\\resources\\bookImgUpload\\";
		
		// 상품 코드 생성
		String bookCode = searchService.selectBookCode();
		
		// 다음에 올 이미지 코드 숫자 생성
		int nextNum = searchService.selectImgCodeNum();
		
		try {
			
			// 업로드 할 파일명 설정
			String uploadFileName = UploadUtil.getNowDateTime() + "_" + file.getOriginalFilename();
			// 지정한 경로에 파일 첨부
			file.transferTo(new File(uploadPath + uploadFileName));
			
			String imgCode = "IMG_" + String.format("%03d", nextNum++);
			
			bookVO.setBookImgVO(new BookImgVO(imgCode, file.getOriginalFilename(), uploadFileName, bookCode));
			bookVO.setBookCode(bookCode);
			
		} catch (IllegalStateException e) {
			// 업로드 예외 발생 시
			e.printStackTrace();
		} catch (IOException e) {
			// 파일 입출력 예외 발생 시
			e.printStackTrace();
		}
		
		return "redirect:/libManage2/regBookForm";
		
	}
	
	
}