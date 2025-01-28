import { dependencies } from '@module-federation/enhanced';
import { ModuleFederationConfig } from '@nx/module-federation';

const config: ModuleFederationConfig = {
  name: 'side',
  exposes: {
    './Module': './src/remote-entry.ts',
  },
  shared(libraryName, sharedConfig) {
    // For React:
    if (libraryName === 'react' || libraryName === 'react-dom') {
      return {
        singleton: true,
        strictVersion: true,
        requiredVersion: dependencies[libraryName],
      };
    }

    if (libraryName === '@frontend/notification-lib') {
      return {
        singleton: true,
      };
    }
  },
};

/**
 * Nx requires a default export of the config to allow correct resolution of the module federation graph.
 **/
export default config;
