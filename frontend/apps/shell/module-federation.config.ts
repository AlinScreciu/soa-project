import { dependencies } from '@module-federation/enhanced';
import { ModuleFederationConfig } from '@nx/module-federation';

const config: ModuleFederationConfig = {
  name: 'shell',

  // List your remotes here
  remotes: ['register', 'posts', 'side', 'profile', 'notifications'],
  /**
   * Instead of `['']`, you should share the libraries you want to load
   * exactly once at runtime. In most Nx + React setups, that means:
   *   - react
   *   - react-dom
   *   - any other library used in both the host and remote
   */
  shared(libraryName, sharedConfig) {
    // For React:
    if (libraryName === 'react' || libraryName === 'react-dom') {
      return {
        singleton: true,
        strictVersion: true,
        requiredVersion: dependencies[libraryName],
      };
    }

    // For react-stomp-hooks:
    if (libraryName === 'react-stomp-hooks') {
      return {
        singleton: true,
        strictVersion: false,
        requiredVersion: dependencies['react-stomp-hooks'],
      };
    }

    if (libraryName === '@frontend/notification-lib') {
      return {
        singleton: true,
      };
    }

    // If you want to override more packages, add them here.
    // Otherwise, return the default config Nx provides:
    return sharedConfig;
  },
};

/**
 * Nx requires a default export of the config to allow correct resolution of the module federation graph.
 **/
export default config;
