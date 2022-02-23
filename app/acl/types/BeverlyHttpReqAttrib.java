package acl.types;

import play.libs.typedmap.TypedKey;

public abstract class BeverlyHttpReqAttrib {
    public static final TypedKey<BeverlyHttpAuthObject> USER = TypedKey.create("user");
}

