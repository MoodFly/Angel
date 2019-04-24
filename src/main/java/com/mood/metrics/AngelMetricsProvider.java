package com.mood.metrics;
/**
 * <p>
 * Example:
 * {@code
 *     ServiceLoader<AngelMetricsProvider> providers = ServiceLoader.load(AngelMetricsProvider.class);
 *     List<AngelMetricsProvider> list = new ArrayList<AngelMetricsProvider>();
 *     for (AngelMetricsProvider provider : providers) {
 *         list.add(provider);
 *     }
 * }
 * </p>
 * @author: by Mood
 * @date: 2019-04-24 14:33:17
 * @Description: 通过spi机制增加,参照canal的收集机制进行加载.当然这个地方其实使用spi有点大材小用的意思，使用Spring提供的如下ConditionalOnXXX也是可以是实现的。
 * @ConditionalOnBean（仅仅在当前上下文中存在某个对象时，才会实例化一个Bean）
 * @ConditionalOnClass（某个class位于类路径上，才会实例化一个Bean）
 * @ConditionalOnExpression（当表达式为true的时候，才会实例化一个Bean）
 * @ConditionalOnMissingBean（仅仅在当前上下文中不存在某个对象时，才会实例化一个Bean）
 * @ConditionalOnMissingClass（某个class类路径上不存在的时候，才会实例化一个Bean）
 * @version: 1.0
 */
public interface AngelMetricsProvider {
    Angelmetrics getService();
}
