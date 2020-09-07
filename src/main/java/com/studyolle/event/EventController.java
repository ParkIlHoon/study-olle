package com.studyolle.event;

import com.studyolle.account.CurrentUser;
import com.studyolle.domain.Account;
import com.studyolle.domain.Event;
import com.studyolle.domain.Study;
import com.studyolle.study.StudyService;
import lombok.RequiredArgsConstructor;
import org.dom4j.rule.Mode;
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
    private final EventService eventService;
    private final EventRepository eventRepository;
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
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/events/{id}")
    public String getEvent(@CurrentUser Account account,
                           @PathVariable String path,
                           @PathVariable Long id,
                           Model model)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        Event event = eventRepository.findById(id).orElseThrow();

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
        Study study = studyService.getStudyForUpdateSelf(account, path);

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
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/events/{id}/edit")
    public String updateEventForm (@CurrentUser Account account,
                                   @PathVariable String path,
                                   @PathVariable Long id,
                                   Model model)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        Event event = eventRepository.findById(id).orElseThrow();

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
     * @param id
     * @param eventForm
     * @param errors
     * @param model
     * @param attributes
     * @return
     */
    @PostMapping("/events/{id}/edit")
    public String updateEventSubmit(@CurrentUser Account account,
                                    @PathVariable String path,
                                    @PathVariable Long id,
                                    @Valid @ModelAttribute EventForm eventForm,
                                    Errors errors,
                                    Model model,
                                    RedirectAttributes attributes)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        Event event = eventRepository.findById(id).orElseThrow();
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
     * @param id
     * @return
     */
    @DeleteMapping("/events/{id}")
    public String cancelEvent(@CurrentUser Account account,
                              @PathVariable String path,
                              @PathVariable Long id)
    {
        Study study = studyService.getStudyForUpdateSelf(account, path);
        Event event = eventRepository.findById(id).orElseThrow();

        eventService.deleteEvent(event);
        return "redirect:/study/" + study.getEncodePath() + "/events";
    }
}
