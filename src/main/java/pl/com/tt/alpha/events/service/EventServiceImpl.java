package pl.com.tt.alpha.events.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.category.domain.CategoryEntity;
import pl.com.tt.alpha.category.repository.CategoryRepository;
import pl.com.tt.alpha.common.exception.ObjectNotFoundException;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.mapper.EventMapper;
import pl.com.tt.alpha.events.repository.EventRepository;
import pl.com.tt.alpha.events.vm.EventFilteredVm;
import pl.com.tt.alpha.events.vm.EventMembersVm;
import pl.com.tt.alpha.events.vm.EventVm;
import pl.com.tt.alpha.events.vm.EventVmToUpdate;
import pl.com.tt.alpha.filters.SpecificationBuilder;
import pl.com.tt.alpha.security.SecurityUtils;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class EventServiceImpl implements EventService{

        private EventRepository eventRepository;
        private EventMapper eventMapper;
        private UserRepository userRepository;
        private CategoryRepository categoryRepository;
        private EventMembersService eventMembersService;

    @Override
    public EventVm createEvent(EventVm eventVm) {
        EventEntity event = eventMapper.toEntity(eventVm);

        String currentUser = SecurityUtils.getCurrentUserOnlyLogin();
        UserEntity author = userRepository.getOneByLogin(currentUser);
        event.setAuthor(author);
        CategoryEntity category = categoryRepository.getOne(eventVm.getCategory());
        event.setCategory(category);
        EventVm saved = eventMapper.toVm(eventRepository.save(event));
        EventMembersVm eventMembersVm = new EventMembersVm();
        eventMembersVm.setEventId(saved.getId());
        eventMembersVm.setUserId(author.getId());
        eventMembersService.addToEvent(eventMembersVm);
        log.debug("Created Information for UserEntity: {}", saved);
        return saved;
    }

    @Override
    public Optional<EventVm> updateEvent(Long eventId, EventVmToUpdate eventVm) {
        Optional<EventEntity> existingEvent = eventRepository.findById(eventId);
        if(!existingEvent.isPresent())
            throw new ObjectNotFoundException("Event doesn't exist");
        return Optional.of(eventRepository.findById(eventId)).filter(Optional::isPresent).map(Optional::get).map(event ->{
            event.setName(eventVm.getName());
            event.setPlaceId(eventVm.getPlaceId());
            event.setCity(eventVm.getCity());
            event.setCategory(categoryRepository.findOneById(eventVm.getCategory()).get());
            event.setDate(eventVm.getDate());
            event.setDescription(eventVm.getDescription());
            return event;
        }).map(eventMapper::toVm);

    }

    @Override
    public void deleteEventById(Long eventId) {
        if (!eventRepository.findById(eventId).isPresent())
            throw new ObjectNotFoundException("Event with id: " + eventId + " doesn't exist!");
        eventMembersService.deleteAllMembersFromEventMemberTable(eventId);
        EventEntity entity = eventRepository.getOneById(eventId);
        entity.setAuthor(null);
        entity.setCategory(null);
        eventRepository.save(entity);
        eventRepository.deleteById(eventId);
        log.debug("Delete Category: ", eventId);
    }

    @Override
    public List<EventEntity> getAllEvent() {
        List<EventEntity> list = eventRepository.findAll();
        if(list.isEmpty())
            throw new ObjectNotFoundException("There are not category");
        return eventRepository.findAll();
    }

    @Override
    public Optional<EventVm> getEventById(Long eventId) {
        Optional<EventEntity> categoryEntity = eventRepository.findOneById(eventId);
        if(!categoryEntity.isPresent())
            throw new ObjectNotFoundException("Category doesn't exist");
        Optional<EventVm> categoryVm = categoryEntity.map(eventMapper::toVm);

        return categoryVm;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<EventEntity> getFilteredEvent( EventFilteredVm eventFilteredVm, Pageable pageable) {
        Specification<EventEntity> specifications = SpecificationBuilder.getInstance().getSpecification(eventFilteredVm);
        if (nonNull(specifications)) {
            return eventRepository.findAll(specifications, pageable);
        }
        return eventRepository.findAll(pageable);
    }

}
