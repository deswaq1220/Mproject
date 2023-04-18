Skip to content
Search or jump to…
Pull requests
Issues
Codespaces
Marketplace
Explore
 
@deswaq1220 
wlswo
/
MoCo
Public
Fork your own copy of wlswo/MoCo
Code
Issues
Pull requests
Actions
Projects
Security
Insights
Beta Try the new code view
MoCo/src/main/java/com/board/board/controller/BoardController.java /
@wlswo
wlswo [UPDATE] springDoc - openApi 추가
Latest commit c2ff8f1 on Oct 21, 2022
 History
 1 contributor
380 lines (323 sloc)  18.6 KB
 

package com.board.board.controller;


import com.board.board.config.LoginUser;
import com.board.board.config.auth.SessionUser;
import com.board.board.domain.HashTag;
import com.board.board.dto.*;
import com.board.board.service.board.BoardService;
import com.board.board.service.board.CommentService;
import com.board.board.service.board.LikeService;
import com.board.board.service.board.RecruitService;
import com.board.board.service.hashTag.HashTagService;
import io.github.furstenheim.CopyDown;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/* 게시판 */
@AllArgsConstructor
@Controller
@RequestMapping("board")
public class BoardController {
    private final BoardService boardService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final HashTagService hashTagService;
    private final RecruitService recruitService;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    /* ----- Board 📋 ----- */
    @Operation(summary = "모집중인 게시글 페이지 반환", description = "모집중인 게시글 리스트 데이터를 담아 페이지를 반환합니다.")
    @GetMapping({"","/list"})
    public String list(@Parameter(description = "타임리프에 반환하기 위한 객체") Model model, @Parameter(description = "반환할 게시글의 페이지 번호") @RequestParam(value = "page", defaultValue = "1") Integer pageNum) {
        List<BoardListVo> boardList = boardService.getBoardListOnRecruit(pageNum);
        Integer pageList = boardService.getPageList(pageNum);

        model.addAttribute("boardList",boardList);
        model.addAttribute("totalPage", pageList);

        return "board/list";
    }

    /* RETURN PAGE - 게시글 목록 페이지 (전체 게시글) */
    @Operation(summary = "모든 게시글 페이지 반환", description = "전체 게시글 리스트 데이터를 담아 페이지를 반환합니다.")
    @GetMapping("/list-all")
    public String recruitOn(@Parameter(description = "반환할 게시글의 페이지번호") @RequestParam(value = "page", defaultValue = "1") Integer pageNum , @Parameter(description = "타임리프에 반환하기 위한 객체") Model model) {
        List<BoardListVo> boardDtoList = boardService.getBoardlist(pageNum);
        model.addAttribute("boardList",boardDtoList);
        return "board/list";
    }

    /* RETURN PAGE - 글작성 페이지 */
    @Operation(summary = "글 작성 페이지 반환", description = "글쓰기 페이지를 반환합니다.")
    @GetMapping("/write")
    public String write(@Parameter(description = "현재 로그인된 사용자 식별")@LoginUser SessionUser sessionUser){
        if(!sessionUser.isNameCheck()){
            return "login/OauthNameCheck";
        }
        return "board/write";
    }

    /* RETURN PAGE - 글읽기 페이지 */
    @Operation(summary = "게시글 상세 페이지 반환", description = "게시글을 클릭 했을때 해당 게시글의 상세 페이지로 이동합니다.")
    @GetMapping("/detail/{boardId}")
    public String detail(@Parameter(description = "해당번호를 가진 게시글을 읽습니다.")@PathVariable("boardId") Long boardId, @Parameter(description = "현재 로그인된 사용자를 식별")@LoginUser SessionUser sessionUser, Model model, @Parameter(description = "조회수 중복 방지를 위해 쿠키값을 가져오기 위한 파라미터입니다. ")HttpServletRequest request, @Parameter(description = "조회수 중복 방지를 위해 쿠키값을 가져오기 위한 파라미터입니다. ") HttpServletResponse response) {
        BoardDto.Response boardDTO = boardService.findById(boardId);
        List<CommentDto.Response> comments = commentService.convertNestedStructure(boardDTO.getComments());

        /* 쿠키 관련 */
        Cookie oldCookie = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewCookie")) {
                    oldCookie = cookie;
                }
            }
        }
        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + boardId.toString() + "]")) {
                boardService.updateView(boardId); /* 조회수++ */
                oldCookie.setValue(oldCookie.getValue() + "[" + boardId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24); /* 유효시간 */
                response.addCookie(oldCookie);
            }
        }else {
            Cookie newCookie = new Cookie("viewCookie", "[" + boardId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 2);
            response.addCookie(newCookie);
            boardService.updateView(boardId); /* 조회수++ */
        }

        /* 좋아요 관련 */
        Long like_count = likeService.findLikeCount(boardId);
        model.addAttribute("likeCount", like_count);

        if(sessionUser != null){
            if(likeService.findLike(sessionUser.getId(), boardId)){
                model.addAttribute("isLiked",true);
            }else {
                model.addAttribute("isLiked", false);
            }
        }else {
            model.addAttribute("isLiked", false);
        }

        /* 댓글 리스트 */
        if(comments != null && !comments.isEmpty()) {
            model.addAttribute("comments",comments);
        }

        /* 사용자 관련 */
        if(sessionUser != null){
            /* 게시글 작성자 본인인지 확인 */
            if(boardDTO.getUserId().equals(sessionUser.getId())) {
                model.addAttribute("iswriter",true);
            }else {
                model.addAttribute("iswriter",false);
            }

            /* 댓글 작성자 본인인지 확인 */
            for (int i = 0; i < comments.size(); i++) {
                //댓글 작성자 id와 현재 사용자 id를 비교
                boolean iswriterComment = comments.get(i).getUserId().equals(sessionUser.getId());
                model.addAttribute("iswriterComment", iswriterComment);
            }
        }
        /* 현재 참가 인원 */
        Long joinUsers = recruitService.countToJoinUsers(boardId);
        model.addAttribute("joinUsers",joinUsers);
        model.addAttribute("boardDto",boardDTO);
        return "board/detail";
    }

    /* RETURN PAGE - 게시글 수정 페이지 */
    @Operation(summary = "게시글 수정 페이지 반환", description = "게시글 수정 화면으로 이동합니다.")
    @GetMapping("/edit/{boardId}")
    public String edit(@Parameter(description = "해당 번호를 가진 게시글을 수정합니다.") @PathVariable("boardId") Long no, Model model,@Parameter(description = "현재 로그인된 사용자를 식별") @LoginUser SessionUser sessionUser) {
        BoardDto.Response boardDTO = boardService.getPost(no);

        if( !boardDTO.getUserId().equals(sessionUser.getId()) ) {
            return "error/404error";
        }

        /* 해시태그 내용만 Filter */
        Set<HashTag> hashTags = hashTagService.getTags(no);
        Iterator<HashTag> hashTagContents = hashTags.iterator();
        StringBuilder sb = new StringBuilder();

        while (hashTagContents.hasNext()) {
            sb.append(hashTagContents.next().getTagcontent()).append(",");
        }

        /* Html -> MarkDown */
        CopyDown converter = new CopyDown();
        String myHtml = boardDTO.getContent();
        boardDTO.setContent(converter.convert(myHtml));

        model.addAttribute("boardDto",boardDTO);
        model.addAttribute("hashTags", sb);
        model.addAttribute("no", no);
        return "board/update";
    }

    /* READ - 무한스크롤 AJAX */
    @Operation(summary = "다음 페이지의 게시글들을 반환", description = "가져올 페이지번호를 받아 모집중인 게시글들을 반환합니다.")
    @GetMapping("/list-next/{page}/{isRecruitOn}")
    public ResponseEntity listJson(@Parameter(description = "가져올 게시글들의 페이지 번호입니다.") @PathVariable("page") Integer pageNum,@Parameter(description = "모집중인 게시글을 구분하기 위한 파라미터입니다.") @PathVariable("isRecruitOn") Boolean isRecruitOn) {
        List<BoardListVo> boardList = new ArrayList<>();
        if(isRecruitOn) { /* 모집중만 */
            boardList = boardService.getBoardListOnRecruit(pageNum);
        }else {           /* 전체 게시글 */
            boardList = boardService.getBoardlist(pageNum);
        }
        return ResponseEntity.ok(boardList);
    }

    /* CREATE - 글작성 */
    @Operation(summary = "게시글 작성", description = "신규 게시글을 등록합니다.")
    @PostMapping("/write")
    public String write(@Parameter(description = "게시글의 정보가 담긴 Request 객체입니다.") @Valid BoardDto.Request boardDto, Errors errors , @Parameter(description = "현재 로그인된 사용자를 식별")@LoginUser SessionUser sessionUser, Model model, @Parameter(description = "해시태그의 정보를 String 으로 받습니다. 후에 문자열 파싱을 통해 DB에 저장합니다.") @RequestParam(value = "tags", required = false) String tags) {
        /* 글작성 유효성 검사 */
        if(errors.hasErrors()) {
            /* 글작성 실패시 입력 데이터 값 유지 */
            model.addAttribute("boardDto",boardDto) ;
            /* 유효성 통과 못한 필드와 메세지를 핸들링 */
            model.addAttribute("error","제목을 입력해주세요.");
            return "board/write";
        }

        /* 썸네일 부재시 디폴트값 설정 */
        if (boardDto.getThumbnail().equals("") || boardDto.getThumbnail().equals(null)){
            boardDto.setThumbnail("/img/thumbnail.png");
        }
        boardDto.setWriter(sessionUser.getName());
        Long board_Id = boardService.savePost(sessionUser.getName(),boardDto);

        /* 해시태그 저장 */
        if(!tags.isEmpty()) {
            List<HashTagDto.Request> hashTagDtoList = new ArrayList<>();
            try{
                JSONParser parser = new JSONParser();
                JSONArray json = (JSONArray) parser.parse(tags);
                json.forEach(item -> {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(item.toString());
                    HashTagDto.Request hashTagDto = new HashTagDto.Request();
                    hashTagDto.setTagcontent(jsonObject.get("value").toString());
                    hashTagDtoList.add(hashTagDto);
                });
                hashTagService.SaveAll(board_Id,hashTagDtoList);
            }catch (ParseException e) {
                log.info(e.getMessage());
            }
        }

        return "redirect:/board/list";
    }

    /* UPDATE - 게시글 수정 */
    @Operation(summary = "게시글 수정", description = "게시글을 수정 합니다. 수정 성공시 모집하기 페이지로 리다이렉트 됩니다.")
    @PutMapping("/edit/{no}")
    public String update(@Parameter(description = "해당 번호를 가진 게시글을 수정합니다.") @PathVariable("no") Long no, @Parameter(description = "수정된 게시글의 정보가 담긴 Request 객체 입니다.") @Valid BoardDto.Request boardDto, @Parameter(description = "해시태그의 정보를 String 으로 받습니다. 후에 문자열 파싱을 통해 DB에 저장합니다.") @RequestParam(value = "tags",required = false) String tags ,@LoginUser SessionUser sessionUser) {
        if(!sessionUser.getId().equals(boardService.getPost(no).getUserId())) {
            return "error/404error";
        }

        boardDto.setWriter(sessionUser.getName());
        boardService.updatePost(no,boardDto);

        /* 해시태그 수정 */
        if(!tags.isEmpty()) {
            HashSet<String> hashTagList = new HashSet<>();
            try{
                JSONParser parser = new JSONParser();
                JSONArray json = (JSONArray) parser.parse(tags);
                json.forEach(item -> {
                    JSONObject jsonObject = (JSONObject) JSONValue.parse(item.toString());
                    hashTagList.add(jsonObject.get("value").toString());
                });

                /* 기존 해시태그와 비교 */
                HashSet<HashTag> OriginHashTags =  hashTagService.getTags(no);
                HashSet<String> OriginHashTagsContent = new HashSet<>();
                OriginHashTags.forEach(item -> {
                    OriginHashTagsContent.add(item.getTagcontent());
                });

                /* 추가된 해시태그 */
                HashSet<String> AddTags = new HashSet<>(hashTagList);  // s1으로 substract 생성
                AddTags.removeAll(OriginHashTagsContent);              // 차집합 수행
                if(!AddTags.isEmpty()) {
                    List<HashTagDto.Request> hashTagDtoList = new ArrayList<>();
                    AddTags.forEach(item -> {
                        HashTagDto.Request hashTagDto = new HashTagDto.Request();
                        hashTagDto.setTagcontent(item);
                        hashTagDtoList.add(hashTagDto);
                    });
                    hashTagService.SaveAll(no,hashTagDtoList);
                }

                /* 삭제된 해시태그 */
                HashSet<String> SubTags = new HashSet<>(OriginHashTagsContent);  // s1으로 substract 생성
                SubTags.removeAll(hashTagList);                                  // 차집합 수행
                List<String> setTolist = new ArrayList<>(SubTags);

                if(!SubTags.isEmpty()) {
                    hashTagService.DeleteAll(no,setTolist);
                }


            }catch (ParseException e) {
                log.info(e.getMessage());
            }
        }

        return "redirect:/board/list";
    }

    /* DELETE - 게시글 삭제 */
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. 삭제 성공시 모집하기 페이지로 리다이렉트 됩니다.")
    @DeleteMapping("/{boardId}")
    public String delete(@Parameter(description = "해당 번호를 가진 게시글을 삭제합니다.") @PathVariable("boardId") Long boardId, @LoginUser SessionUser sessionUser) {
        if(!sessionUser.getId().equals(boardService.getPost(boardId).getUserId())){
            return "/error/404error";
        }

        boardService.deletePost(boardId);
        return "redirect:/board/list";
    }

    /* READ - 검색 */
    @Operation(summary = "게시글 검색", description = "게시글을 검색합니다. 모집중인 게시글만 반환합니다.")
    @GetMapping("/search")
    public String search(@Parameter(description = "검색한 게시글들의 페이지 번호입니다. 기본값으로는 1 입니다.") @RequestParam(value = "page", defaultValue = "1") Integer pageNum , @Parameter(description = "검색할 키워드가 담긴 파라미터입니다.") @RequestParam(value = "keyword") String keyword, Model model) {
        List<BoardListVo> boardDtoList = boardService.searchPosts(pageNum, keyword);
        model.addAttribute("boardList", boardDtoList);
        return "/board/list";
    }

    /* CREATE - 스터디 참가 */
    @Operation(summary = "스터디 참가", description = "스터디에 참가합니다. 응답으로는 200 , 400 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "참가 성공의 경우 응답입니다."),
            @ApiResponse(responseCode = "400", description = "참가 실패의 경우 응답입니다."),
    })
    @PostMapping("/recruit/{boardId}/{userId}")
    public ResponseEntity recruitSave(@Parameter(description = "참가하는 게시글 번호입니다.") @PathVariable Long boardId, @Parameter(description = "참가하는 사용자의 번호입니다.") @PathVariable Long userId, @LoginUser SessionUser sessionUser) {
        if (!sessionUser.getId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        RecruitDto.Request dto = new RecruitDto.Request();

        boolean isDuplicate = recruitService.isDuplicate(boardId,userId);
        if(isDuplicate) {
            return ResponseEntity.badRequest().body("이미 신청하였습니다.");
        }
        return ResponseEntity.ok(recruitService.Join(boardId,userId,dto));
    }

    /* DELETE - 모집 마감 취소 */
    @DeleteMapping("/recruit-cancel/{boardId}/{userId}")
    @Operation(summary = "모각코 모집 마감 취소", description = "모집 마감을 취소합니다. 게시글 작성자만 호출할수 있습니다.")
    public ResponseEntity recruitDelete(@Parameter(description = "모집 마감을 취소할 게시글의 번호입니다.") @PathVariable Long boardId, @Parameter(description = "모집 취소를 누른 사용자의 번호입니다.") @PathVariable Long userId, @LoginUser SessionUser sessionUser) {
        if(!sessionUser.getId().equals(userId)) {
            return ResponseEntity.badRequest().build();
        }

        int rows = recruitService.joinCancel(boardId,userId);
        int status = rows == 1 ? 200 : 400;
        return ResponseEntity.status(status).build();
    }

    /* UPDATE - 모집 마감 */
    @Operation(summary = "모각코 모집을 마감", description = "모각코 모집을 마감합니다. 게시글 작성자만 호출할수 있습니다.")
    @PatchMapping("/recruit-off/{boardId}")
    public ResponseEntity recruitClose(@Parameter(description = "해당 번호를 가진 게시글에 대해 요청합니다.") @PathVariable Long boardId, @LoginUser SessionUser sessionUser) {
        if(!sessionUser.getId().equals(boardService.getPost(boardId))) {
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(boardService.updateFull(boardId));
    }

}









Footer
© 2023 GitHub, Inc.
Footer navigation
Terms
Privacy
Security
Status
Docs
Contact GitHub
Pricing
API
Training
Blog
About
MoCo/BoardController.java at fc6398cb0ac90d1c976368061d1f1119985bce45 · wlswo/MoCo