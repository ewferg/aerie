import type { Pool, PoolConfig } from 'pg';
import pg from 'pg';
import getLogger from './utils/logger.js';
import { getEnv } from './env.js';

const { Pool: DbPool } = pg;

const {
  POSTGRES_AERIE_EXPANSION_DB,
  POSTGRES_HOST: host,
  POSTGRES_PASSWORD: password,
  POSTGRES_PORT: port,
  POSTGRES_USER: user,
} = getEnv();

const logger = getLogger('packages/db/db');

export class DbExpansion {
  private static pool: Pool;

  static getDb(): Pool {
    return DbExpansion.pool;
  }

  static init() {
    try {
      const config: PoolConfig = {
        database: POSTGRES_AERIE_EXPANSION_DB,
        host,
        password,
        port: parseInt(port, 10),
        user,
      };

      logger.info(`Postgres Config:`);
      logger.info(`
      {
         database: ${config.database},
         host: ${config.host},
         port: ${config.port}
      }`
      );

      DbExpansion.pool = new DbPool(config);
    } catch (error) {
      logger.error(error);
    }
  }
}
