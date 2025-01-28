import requests
from flask import Request, jsonify
from parliament import Context
from typing import List

# Configuration variables
FOLLOW_SERVICE_URL = "http://follow-service-follow-service-chart.default.svc.cluster.local:9092"
NOTIFICATION_SERVICE_URL = "http://notification-service.default.svc.cluster.local:9093/api/notification"
FOLLOW_SERVICE_TOKEN = "notification-service"
NOTIFICATION_SERVICE_TOKEN = "api-token"


def get_user_followers(user_id: str) -> List[str]:
    """
    Fetches the list of followers for a given user from the Follow Service.

    Args:
        user_id (str): The ID of the user whose followers need to be fetched.

    Returns:
        List[str]: A list of follower user IDs.
    """
    headers = {"Authorization": f"Bearer {FOLLOW_SERVICE_TOKEN}"}
    try:
        response = requests.get(
            f"{FOLLOW_SERVICE_URL}/api/follow/{user_id}/followers", headers=headers)
        response.raise_for_status()
        followers = response.json()
        print(f"Followers fetched for user {user_id}: {followers}")
        return followers
    except requests.exceptions.RequestException as e:
        print(f"Error fetching followers for user {user_id}: {e}")
        return []


def create_notification_content_for_post_author(event: dict) -> str:
    """
    Creates the notification content based on the event data.

    Args:
        event (dict): The event containing userId, postAuthorId, postId, and type.

    Returns:
        str: Notification content with encoded postId.
    """
    user_id = event.get("userId")
    post_id = event.get("postId")
    notification_type = event.get("type", "like").lower()
    notification_content = f"@{user_id} {
        notification_type}d your !postId={post_id}"
    print(f"Notification content created: {notification_content}")
    return notification_content


def create_notification_content_for_followers(event: dict) -> str:
    """
    Creates the notification content based on the event data.

    Args:
        event (dict): The event containing userId, postAuthorId, postId, and type.

    Returns:
        str: Notification content with encoded postId.
    """
    user_id = event.get("userId")
    post_id = event.get("postId")
    notification_type = event.get("type", "like").lower()
    notification_content = f"@{user_id} {
        notification_type} this !postId={post_id}"
    print(f"Notification content created: {notification_content}")
    return notification_content


def trigger_notification(user_id: str, content: str):
    """
    Sends a notification to a user by making a request to the Notification Service.

    Args:
        user_id (str): The ID of the user to receive the notification.
        content (str): The content of the notification.
    """
    notification = {
        "userId": user_id,
        "content": content,
        "delivered": False
    }

    headers = {"Authorization": f"Bearer {NOTIFICATION_SERVICE_TOKEN}"}
    try:
        response = requests.post(
            NOTIFICATION_SERVICE_URL + "/trigger", json=notification, headers=headers)
        response.raise_for_status()
        print(f"Notification successfully sent to user {
              user_id}. Response: {response.status_code}")
    except requests.exceptions.RequestException as e:
        print(response.text)
        print(f"Error sending notification to user {user_id}: {e}")


def main(context: Context):
    """
    Handles incoming requests and processes CloudEvents.
    """
    req: Request = context.request

    try:
        # Attempt to parse the JSON body
        event = req.get_json()
        if not event:
            raise ValueError("Empty JSON body")

        print(f"Received event: {event}")

        # Extract event details
        post_author_id = event.get("postAuthorId")
        if not post_author_id:
            raise ValueError("Missing postAuthorId in event data")

        user_id = event.get("userId")
        if not user_id:
            raise ValueError("Missing userId in event data")

        if user_id == post_author_id:
            return ({"message": "Won't notify when user likes own post"}), 200

        # Fetch followers of the user
        followers = filter(lambda follower: follower !=
                           post_author_id, get_user_followers(user_id))
        if not followers:
            print(f"No followers found for user {user_id}")
            return jsonify({"message": "No followers to notify"}), 200

        # Create notification content for author
        content_for_author = create_notification_content_for_post_author(event)
        trigger_notification(post_author_id, content=content_for_author)

        content_for_follower = create_notification_content_for_followers(event)
        # Trigger notifications for each follower
        for follower in followers:
            trigger_notification(follower, content_for_follower)

        print(f"Notifications successfully triggered for followers of user {
              post_author_id}")
        return jsonify({"message": "Notifications triggered successfully"}), 200

    except ValueError as ve:
        print(f"ValueError: {ve}")
        return jsonify({"error": str(ve)}), 400

    except Exception as e:
        print(f"Unexpected error: {e}")
        return jsonify({"error": "Internal server error"}), 500
