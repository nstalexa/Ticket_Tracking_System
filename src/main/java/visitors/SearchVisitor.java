package visitors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import core.*;
import fileio.SearchInput;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

import static core.AppSystem.MAPPER;

public class SearchVisitor implements CommandVisitor {

    @Setter
    private ObjectNode output;
    private Search command;
    private Ticket ticket;
    private SearchInput searchInput;
    private SearchContext context;
    public SearchVisitor(Search command, Ticket ticket, SearchInput searchInput) {
        this.command = command;
        this.ticket = ticket;
        this.searchInput = searchInput;
    }


    @Override
    public void visit(UserManager manager, AppSystem system) {
        context = new SearchContext(manager, command.getTs(), system);
        context.setMatchingWords(new HashMap<>());
        if ("TICKET".equals(searchInput.getSearchType()))  {
            List<Ticket> tickets = new ArrayList<>();
            for(Ticket i : system.getTickets()) {
                tickets.add(i);
            }

            tickets.sort(new Comparator<Ticket>() {
                @Override
                public int compare(Ticket o1, Ticket o2) {
                    if(o1.getCreatedAt().isBefore(o2.getCreatedAt())) {
                        return -1;
                    } else if(o1.getCreatedAt().isAfter(o2.getCreatedAt())) {
                        return 1;
                    } else {
                        if(o1.getId() < o2.getId()) {
                            return -1;
                        } else if(o1.getId() > o2.getId()){
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }
            });
            tickets = tickets.stream().filter(searchParseTicket(searchInput, context).stream().reduce(t -> true, Predicate::and)).toList();

            ArrayNode arr = MAPPER.createArrayNode();
            for(Ticket i : tickets) {
                ObjectNode ret = MAPPER.createObjectNode();
                ret.put("id", i.getId());
                ret.put("type", i.getType().toString());
                ret.put("title", i.getTitle());
                ret.put("businessPriority", i.getBusinessPriority().toString());
                ret.put("status", i.getStatus().toString());
                ret.put("createdAt", i.getCreatedAt().toString());
                ret.put("solvedAt", "");
                ret.put("reportedBy", i.getReportedBy());

                ArrayNode matching = MAPPER.createArrayNode();
                List<String> matchedWordsList = context.getMatchingWords().get(i.getId());

                if (matchedWordsList != null) {
                    for(String wrd : matchedWordsList) {
                        matching.add(wrd);
                    }
                }

                ret.put("matchingWords", matching);

                arr.add(ret);
            }
            output.put("searchType", "TICKET");
            output.put("results", arr);
        } else if ("DEVELOPER".equals(searchInput.getSearchType())) {
            List<UserDeveloper> devList = new ArrayList<>();
            for(String dev : manager.getSubordinates()) {
                devList.add(system.getDevById(dev));
            }

            devList.sort(new Comparator<UserDeveloper>() {
                @Override
                public int compare(UserDeveloper o1, UserDeveloper o2) {
                    return o1.getUsername().compareTo(o2.getUsername());
                }
            });
            devList = devList.stream().filter(searchParseUserDeveloper(searchInput, context).stream().reduce(t -> true, Predicate::and)).toList();


            ArrayNode arr = MAPPER.createArrayNode();
            for(UserDeveloper i : devList) {
                ObjectNode ret = MAPPER.createObjectNode();
                ret.put("username", i.getUsername());
                ret.put("expertiseArea", i.getExpertiseArea().toString());
                ret.put("seniority", i.getSeniority().toString());
                ret.put("performanceScore", i.getPerformanceScore());
                ret.put("hireDate", i.getHireDate());
                arr.add(ret);
            }
            output.put("searchType", "DEVELOPER");
            output.put("results", arr);

        }

    }

    @Override
    public void visit(UserDeveloper dev, AppSystem system) {
        context = new SearchContext(dev, command.getTs(), system);

        List<Ticket> tickets = new ArrayList<>();

        for(Milestone i : system.getMilestones()) {
            if(i.getAssignedDevs().contains(dev.getUsername())) {
                for(Ticket j : i.getTickets()) {
                    if(j.getStatus().equals(Ticket.Status.OPEN)) {
                        tickets.add(j);
                    }
                }
            }
        }


        tickets.sort(new Comparator<Ticket>() {
            @Override
            public int compare(Ticket o1, Ticket o2) {
                if(o1.getCreatedAt().isBefore(o2.getCreatedAt())) {
                    return -1;
                } else if(o1.getCreatedAt().isAfter(o2.getCreatedAt())) {
                    return 1;
                } else {
                    return Integer.compare(o1.getId(), o2.getId());
                }
            }
        });
        tickets = tickets.stream().filter(searchParseTicket(searchInput, context).stream().reduce(t -> true, Predicate::and)).toList();
        ArrayNode arr = MAPPER.createArrayNode();
        for(Ticket i : tickets) {
            ObjectNode ret = MAPPER.createObjectNode();
            ret.put("id", i.getId());
            ret.put("type", i.getType().toString());
            ret.put("title", i.getTitle());
            ret.put("businessPriority", i.getBusinessPriority().toString());
            ret.put("status", i.getStatus().toString());
            ret.put("createdAt", i.getCreatedAt().toString());
            ret.put("solvedAt", "");
            ret.put("reportedBy", i.getReportedBy());
            arr.add(ret);
        }
        output.put("searchType", "TICKET");
        output.put("results", arr);
    }

    @Override
    public void visit(UserReporter reporter, AppSystem system) {

    }

    public List<Predicate<Ticket>> searchParseTicket(SearchInput searchInput, SearchContext context) {
        List<SearchInterface<Ticket>> list = new ArrayList<>();
        if(searchInput.getBusinessPriority() != null) {
            list.add(new BusinessPriorityFilter(Ticket.BusinessPriority.valueOf(searchInput.getBusinessPriority())));
        }

        if(searchInput.getType() != null) {
            list.add(new TypeFilter(Ticket.TicketType.valueOf(searchInput.getType())));
        }

        if(searchInput.getCreatedAt() != null) {
            list.add(new CreatedAtFilter(LocalDate.parse(searchInput.getCreatedAt())));
        }

        if(searchInput.getCreatedBefore() != null) {
            list.add(new CreatedBeforeFilter(LocalDate.parse(searchInput.getCreatedBefore())));
        }

        if(searchInput.getCreatedAfter() != null) {
            list.add(new CreatedAfterFilter(LocalDate.parse(searchInput.getCreatedAfter())));
        }

        if(searchInput.getAvailableForAssignment() != null) {
            list.add(new AvailableForAssignmentFilter());
        }

        if(searchInput.getKeywords() != null) {
            list.add(new KeywordsManagerFilter(searchInput.getKeywords()));
        }

        List<Predicate<Ticket>> predicateList = new ArrayList<>();
        for(SearchInterface i : list) {
            predicateList.add(Ticket -> i.found(Ticket, context));
        }
        return predicateList;
    }

    public List<Predicate<UserDeveloper>> searchParseUserDeveloper(SearchInput searchInput, SearchContext context) {
        List<SearchInterface<UserDeveloper>> list = new ArrayList<>();
        if(searchInput.getExpertiseArea() != null) {
            list.add(new ExpertiseAreaManagerFilter(UserDeveloper.ExpertiseArea.valueOf(searchInput.getExpertiseArea())));
        }

        if(searchInput.getSeniority() != null) {
            list.add(new SeniorityManagerFilter(UserDeveloper.Seniority.valueOf(searchInput.getSeniority())));
        }

        if(searchInput.getPerformanceScoreAbove() != null) {
            list.add(new PerformanceScoreAboveManagerFilter(searchInput.getPerformanceScoreAbove()));
        }

        if(searchInput.getPerformanceScoreBelow() != null) {
            list.add(new PerformanceScoreBelowManagerFilter(searchInput.getPerformanceScoreBelow()));
        }

        List<Predicate<UserDeveloper>> predicateList = new ArrayList<>();
        for(SearchInterface i : list) {
            predicateList.add(UserDeveloper -> i.found(UserDeveloper, context));
        }
        return predicateList;
    }

}
