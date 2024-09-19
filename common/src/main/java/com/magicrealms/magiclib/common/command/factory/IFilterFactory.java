package com.magicrealms.magiclib.common.command.factory;

import com.magicrealms.magiclib.common.command.filter.IChannelFilter;

/**
 * @author Ryan-0916
 * @date 2023-10-01
 **/
public interface IFilterFactory {
    IChannelFilter create(Enum<?> e);
}
