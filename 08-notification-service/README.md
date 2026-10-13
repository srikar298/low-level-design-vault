# 08 - Multi-Channel Notification Engine

## 🎯 Problem Statement
Design an extensible, resilient notification dispatch service:
* Delivers notifications across multiple channels: `EMAIL`, `SMS`, `PUSH_NOTIFICATION`.
* User notification preferences (e.g. User A receives SMS for urgent security alerts, Email for daily digests).
* Automatic fallback routing (if SMS delivery fails, fallback to Push or Email).
* Priority queuing (`HIGH`, `MEDIUM`, `LOW`) with rate limiting per user to prevent notification fatigue.
* Template engine support for personalized notification rendering.

---

## 🏗 Class Diagram

```mermaid
classDiagram
    class NotificationService {
        -Map~ChannelType, INotificationChannel~ channels
        -PreferenceManager preferenceManager
        +sendNotification(NotificationPayload payload) DeliveryResult
    }

    class INotificationChannel {
        <<interface>>
        +send(NotificationPayload payload) boolean
        +supports(ChannelType type) boolean
    }

    class EmailChannel
    class SMSChannel
    class PushChannel

    class NotificationPayload {
        -String userId
        -String recipient
        -String subject
        -String body
        -Priority priority
        -ChannelType preferredChannel
    }

    class ChannelType {
        <<enumeration>>
        EMAIL
        SMS
        PUSH
    }

    INotificationChannel <|.. EmailChannel
    INotificationChannel <|.. SMSChannel
    INotificationChannel <|.. PushChannel
    NotificationService *-- INotificationChannel
    NotificationService o-- NotificationPayload
```

---

## 🚀 Key Implementation Highlights
1. **Strategy & Factory Pattern**: Decoupled `INotificationChannel` implementations with a channel factory.
2. **Chain of Responsibility**: Automatic fallback chain if the primary delivery channel throws a transient gateway error.
3. **Template Rendering**: Parameterized message interpolation before delivery.
