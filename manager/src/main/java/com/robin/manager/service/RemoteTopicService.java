package com.robin.manager.service;

import com.robin.manager.model.TopicWrap;

public interface RemoteTopicService {

    public boolean addTopic(TopicWrap topicWrap);

    public TopicWrap removeTopic(String topic);
}
