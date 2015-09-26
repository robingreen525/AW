import org.fhcrc.honeycomb.metapop.*;

abstract class LBLStaticEnv extends LinearBeforeLoad {
    public LBLStaticEnv(String args[]) { super(args); }

    public void makeEnvChanger() {
        EnvironmentChanger ec = new StaticEnvironment();

        setEnvironmentChanger(ec);
    }
}
