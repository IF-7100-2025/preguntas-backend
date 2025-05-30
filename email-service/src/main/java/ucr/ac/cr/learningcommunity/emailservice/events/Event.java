package ucr.ac.cr.learningcommunity.emailservice.events;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "eventType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RegisterUserEvent.class, name = "NEWUSER")
        , @JsonSubTypes.Type(value = ChangePasswordEvent.class, name = "CHANGEPASSWORD")
        , @JsonSubTypes.Type(value = UpdateProfileEvent.class, name = "CHANGEPROFILE")
})
public abstract class Event<T> {
    private EventType eventType;
    private T data;
    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}