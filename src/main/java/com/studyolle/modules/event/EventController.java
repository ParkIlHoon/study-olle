package com.studyolle.modules.event;

import com.studyolle.modules.account.CurrentUser;
import com.studyolle.modules.account.Account;
import com.studyolle.modules.study.Study;
import com.studyolle.modules.study.StudyRepository;
import com.studyolle.modules.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Event 컨트롤러</h1>
 */
@Controller
@RequestMapping("/study/{path}")
@RequiredArgsConstructor
public class EventController
{
    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;
    private final EventFormValidator eventFormValidator;

    @InitBinder("eventForm")
    void initBinder (WebDataBinder webDataBinder)
    {
        webDataBinder.addValidators(eventFormValidator);
    }

    /**
     * 모임 신규 생성 폼 요청 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/new-event")
    public String newEventForm (@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        model.addAttribute("account", account);
        model.addAttribute("study",study);
        model.addAttribute("eventForm", new EventForm());

        return "event/form";
    }

    /**
     * 모임 신규 생성 요청 메서드
     * @param account
     * @param path
     * @param eventForm
     * @param errors
     * @param model
     * @return
     */
    @PostMapping("/new-event")
    public String newEventSubmit(@CurrentUser Account account,
                                 @PathVariable String path,
                                 @Valid EventForm eventForm,
                                 Errors errors,
                                 Model model)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        if (errors.hasErrors())
        {
            model.addAttribute("account", account);
            model.addAttribute("study",study);
            return "event/form";
        }

        Event event = eventService.createEvent(modelMapper.map(eventForm, Event.class), study, account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    /**
     * 모임 뷰 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverters 를 이용해 id 에 해당하는 Event 엔티티 객체로 받아옴
     * @param model
     * @return
     */
    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account,
                           @PathVariable String path,
                           @PathVariable("id") Event event,
                           Model model)
    {
        Study study = studyRepository.findStudyWithMembersAndManagersByPath(path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("event", event);

        return "event/view";
    }

    /**
     * 모임 목록 조회 메서드
     * @param account
     * @param path
     * @param model
     * @return
     */
    @GetMapping("/events")
    public String getEventList(@CurrentUser Account account, @PathVariable String path, Model model)
    {
        Study study = studyService.getStudy(path);

        List<Event> events = eventRepository.findByStudyOrderByStartDateTime(study);

        List<Event> newEvents = new ArrayList<>();
        List<Event> oldEvents = new ArrayList<>();

        for (Event event : events) {
            if (event.getEndDateTime().isBefore(LocalDateTime.now()))
            {
                oldEvents.add(event);
            }
            else
            {
                newEvents.add(event);
            }
        }

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("oldEvents", oldEvents);
        model.addAttribute("newEvents", newEvents);

        return "study/events";
    }

    /**
     * 모임 정보 수정 뷰 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 id 에 해당하는 Event 엔티티 객체로 받아옴
     * @param model
     * @return
     */
    @GetMapping("/events/{id}/edit")
    public String updateEventForm (@CurrentUser Account account,
                                   @PathVariable String path,
                                   @PathVariable("id") Event event,
                                   Model model)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);

        model.addAttribute("account", account);
        model.addAttribute("study", study);
        model.addAttribute("event", event);
        model.addAttribute("eventForm", modelMapper.map(event, EventForm.class));

        return "event/update-form";
    }

    /**
     * 모임 정보 수정 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 id 에 해당하는 Event 엔티티 객체로 받아옴
     * @param eventForm
     * @param errors
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentUser Account account,
                                    @PathVariable String path,
                                    @PathVariable("id") Event event,
                                    @Valid @ModelAttribute EventForm eventForm,
                                    Errors errors,
                                    Model model,
                                    RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        eventForm.setEventType(event.getEventType());

        eventFormValidator.validateUpdateForm(eventForm, event, errors);

        if (errors.hasErrors())
        {
            model.addAttribute("account", account);
            model.addAttribute("study", study);
            model.addAttribute("event", event);
            return "event/update-form";
        }

        eventService.updateEvent(event, eventForm);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    /**
     * 모임 취소 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 id 에 해당하는 Event 엔티티 객체로 받아옴
     * @return
     */
    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentUser Account account,
                              @PathVariable String path,
                              @PathVariable("id") Event event)
    {
        Study study = studyService.getStudyForJoin(account, path);
        eventService.deleteEvent(event);
        return "redirect:/study/" + study.getEncodePath() + "/events";
    }

    /**
     * 모임 참가신청 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 id 에 해당하는 Event 엔티티 객체로 받아옴
     * @return
     */
    @PostMapping("/events/{id}/enroll")
    public String enrollEvent(@CurrentUser Account account,
                              @PathVariable String path,
                              @PathVariable("id") Event event)
    {
        Study study = studyService.getStudyForEnroll(path);
        eventService.enrollEvent(event, account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    /**
     * 모임 참가취소 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 id 에 해당하는 Event 엔티티 객체로 받아옴
     * @return
     */
    @PostMapping("/events/{id}/disenroll")
    public String disenrollEvent(@CurrentUser Account account,
                                 @PathVariable String path,
                                 @PathVariable("id") Event event)
    {
        Study study = studyService.getStudyForEnroll(path);
        eventService.disenrollEvent(event, account);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    /**
     * 관리자 확인 모임 신청허용 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 eventId 에 해당하는 Event 엔티티 객체로 받아옴
     * @param enrollment Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 enrollmentId 에 해당하는 Enrollment 엔티티 객체로 받아옴
     * @return
     */
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/accept")
    public String acceptEnroll(@CurrentUser Account account,
                               @PathVariable String path,
                               @PathVariable("eventId") Event event,
                               @PathVariable("enrollmentId") Enrollment enrollment)
    {
        Study study = studyService.getStudyForEnroll(path);
        eventService.acceptEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }

    /**
     * 관리자 확인 모임 신청취소 요청 메서드
     * @param account
     * @param path
     * @param event Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 eventId 에 해당하는 Event 엔티티 객체로 받아옴
     * @param enrollment Spring Data JPA 에서 제공해주는 DomainClassConverter 를 이용해 enrollmentId 에 해당하는 Enrollment 엔티티 객체로 받아옴
     * @return
     */
    @GetMapping("/events/{eventId}/enrollments/{enrollmentId}/reject")
    public String rejectEnroll(@CurrentUser Account account,
                               @PathVariable String path,
                               @PathVariable("eventId") Event event,
                               @PathVariable("enrollmentId") Enrollment enrollment)
    {
        Study study = studyService.getStudyForEnroll(path);
        eventService.rejectEnrollment(event, enrollment);
        return "redirect:/study/" + study.getEncodePath() + "/events/" + event.getId();
    }
}
