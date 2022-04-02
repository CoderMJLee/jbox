package io.github.codermjlee.common.log.policy;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import io.github.codermjlee.common.log.Filenames;

/**
 * @author MJ
 */
@SuppressWarnings("rawtypes")
public abstract class ProPolicy extends SizeAndTimeBasedRollingPolicy {

    @Override
    public void setContext(Context context) {
        super.setContext(context);

        setFileNamePattern(Filenames.filepath(this, context) + "/%d{yyy-MM-dd}_%i.log.gz");
        setMaxFileSize(FileSize.valueOf("10MB"));
        setMaxHistory(20);
        setTotalSizeCap(FileSize.valueOf("500MB"));
    }
}
