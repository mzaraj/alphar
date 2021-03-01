package pl.com.tt.alpha.events.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.com.tt.alpha.common.exception.ObjectAlreadyExistException;
import pl.com.tt.alpha.common.exception.ObjectNotFoundException;
import pl.com.tt.alpha.events.domain.EventEntity;
import pl.com.tt.alpha.events.domain.EventMembersEntity;
import pl.com.tt.alpha.events.mapper.EventMembersMapper;
import pl.com.tt.alpha.events.repository.EventMembersRepository;
import pl.com.tt.alpha.events.repository.EventRepository;
import pl.com.tt.alpha.events.vm.EventMembersVm;
import pl.com.tt.alpha.user.domain.UserEntity;
import pl.com.tt.alpha.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@AllArgsConstructor
public class EventMembersServiceImpl implements EventMembersService{

        private EventMembersMapper eventMembersMapper;
        private EventMembersRepository eventMembersRepository;
        private EventRepository eventRepository;
        private UserRepository userRepository;


    @Override
    public EventMembersVm addToEvent(EventMembersVm eventMembersVm) {
        EventMembersEntity eventMembersEntity = new EventMembersEntity();
        EventEntity eventEntity = eventRepository.getOneById(eventMembersVm.getEventId());
        eventMembersEntity.setEvent(eventEntity);
        UserEntity userEntity = userRepository.getOneById(eventMembersVm.getUserId());
        eventMembersEntity.setUser(userEntity);

        List<EventMembersEntity> list = eventMembersRepository.getEventMembersEntitiesByEventId(eventMembersVm.getEventId());
        for(int i =0; i<=list.size(); i++) {
            if(list.isEmpty()) {
                EventMembersVm saved = eventMembersMapper.toVm(eventMembersRepository.save(eventMembersEntity));
                return saved;
            }else if (list.get(i).getUser().getId() == eventMembersVm.getUserId())
                throw new ObjectAlreadyExistException("Już jesteś w tym evencie");
        }
        return null;
    }

    @Override
    public List<EventMembersEntity> getAllMembers(Long eventId) {
        EventEntity nowEvent = eventRepository.getOneById(eventId);
        List<EventMembersEntity> list = eventMembersRepository.findOnlyEventMembersEntitiesByEventId(nowEvent);
        if(list.isEmpty())
            throw new ObjectNotFoundException("There are not members");

        return list;
    }

    @Override
    public void deleteMemberFromEvent(Long eventId, Long userId) {
        List<EventMembersEntity> list = eventMembersRepository.getEventMembersEntitiesByEventId(eventId);
        if (list.isEmpty())
            throw new ObjectNotFoundException("Ten event nie istnieje");

        for(int i =0; i<=list.size(); i++){
           if(list.get(i).getUser().getId() != userId)
               list.remove(i);
        }
        if (list.isEmpty())
            throw new ObjectNotFoundException("Ten użytkownik nie jet dodany do tego eventu");

        eventMembersRepository.saveAll(list);

        eventMembersRepository.deleteByEventIdAndUserId(eventId,userId);
    }

    @Override
    public void deleteAllMembersFromEventMemberTable(Long eventId) {
        eventMembersRepository.deleteByEventId(eventId);
    }


    @Override
    public int getCountElements() {
        List<EventMembersEntity> list = eventMembersRepository.findAll();
        return list.size();
    }


}
